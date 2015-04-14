package marl.ext.tilecoding;

import marl.utility.Config;
import marl.utility.Rand;

/**
 * Taken from: http://webdocs.cs.ualberta.ca/~sutton/tiles2.html, 23/03/2012
 * 
 * External documentation and recommendations on the use of this code is
 * available at http://rlai.net.
 *
 * This is an implementation of grid-style tile codings, based originally on 
 * the UNH CMAC code (see http://www.ece.unh.edu/robots/cmac.htm).
 * Here we provide a procedure, "GetTiles", that maps floating-point and integer
 * variables to a list of tiles. This function is memoryless and requires no
 * setup. We assume that hashing collisions are to be ignored. There may be
 * duplicates in the list of tiles, but this is unlikely if memory-size is
 * large.
 * 
 * The floating-point input variables will be gridded at unit intervals, so
 * generalization will be by 1 in each direction, and any scaling will have
 * to be done externally before calling tiles.  There is no generalization
 * across integer values.
 * 
 * It is recommended by the UNH folks that num-tilings be a power of 2, e.g., 16.
 * 
 * We assume the existence of a function "rand()" that produces successive
 * random integers, of which we use only the low-order bytes.
 */
public class TileCoding
{
	public static final int MAX_NUM_VARS   = 20,
							MAX_NUM_COORDS = 100,
							MaxLONGINT     = 2147483647;
	
	private int 	 nTiles_,
					 nFeatures_,
					 nTilings_,
					 memorySize_;
	private double[] minimumValues_,
					 tileSpacings_;
	
	
	public TileCoding(Config cfg, TileCodingEnvironment<?, ?> env)
	{
	    //
	    nTiles_        = cfg.getInt("num_tiles");
	    nFeatures_     = env.getNumFeatures();
	    nTilings_      = cfg.getInt("num_tilings");
	    memorySize_    = (int)Math.pow(nTiles_, nFeatures_) * nTilings_;

        minimumValues_ = new double[nFeatures_];
	    tileSpacings_  = new double[nFeatures_];
	    for( int i=0; i<nFeatures_; i++ ) {
                   minimumValues_[i] = env.getMinimumValue(i);
	        double range             = env.getMaximumValue(i) - minimumValues_[i];
	        tileSpacings_[i] = range / (double)nTiles_;
	    }
	}

    /**
     * @return The number of tiles being learnt about
     */
	public int getNoTiles() {
	    return (int)(Math.pow(nTiles_, nFeatures_)) * nTilings_;
	}
	
	
	public void getTiles(Tile[] tiles, TileCodingState<?> state)
	{
	    // collect up the features into an array
	    int[]    hashes = new int[nTilings_];
	    double[] doubles = prepareFeatures(state);

	    // get the tile hashes
	    GetTiles(hashes, nTilings_, memorySize_, doubles, nFeatures_);

	    // create the tiles
	    for( int i=0; i<nTilings_; i++ )
	        tiles[i] = new Tile(hashes[i]);
	}
	public void getTiles(int[] tiles, TileCodingState<?> state)
	{
	    // collect up the features into an array
	    double[] doubles = prepareFeatures(state);

	    // get the tile hashes
	    GetTiles(tiles, nTilings_, memorySize_, doubles, nFeatures_);
	}
	protected double[] prepareFeatures(TileCodingState<?> state)
	{
	    double[] doubles = new double[nFeatures_];
	    for( int i=0; i<nFeatures_; i++ )
	        // ( (value - min) / (max - min) ) * ( (max - min) / spacing)
	        // === (value - min) / spacing
	        doubles[i] = (state.getFeature(i) - minimumValues_[i]) / tileSpacings_[i];

	    return doubles;
	}
	
	
	
	


    private int mod(int n, int k) {return (n >= 0) ? n%k : k-1-((-n-1)%k);}
    
    /**
     * @param int tiles[]       provided array contains returned tiles (tile indices) 
     * @param int num_tilings   number of tile indices to be returned
     * @param int memory_size   total number of possible tiles
     * @param double doubles[]    array of doubling point variables
     * @param int num_doubles    number of doubling point variables
     * @param int ints[]        array of integer variables 
     * @param int num_ints      number of integer variables
     */
    private void GetTiles(
        int tiles[],
        int num_tilings,
        int memory_size,
        double doubles[],
        int num_doubles,
        int ints[],
        int num_ints)
    {
        int i,j;
        int[] qstate = new int[MAX_NUM_VARS];
        int[] base   = new int[MAX_NUM_VARS];
        int[] coordinates   = new int[MAX_NUM_VARS * 2 + 1];   /* one interval number per relevant dimension */
        int num_coordinates = num_doubles + num_ints + 1;

        for (i = 0; i<num_ints; i++)
            coordinates[num_doubles+1+i] = ints[i];

        /* quantize state to integers (henceforth, tile widths == num_tilings) */
        for (i = 0; i < num_doubles; i++)
        {
            qstate[i] = (int) Math.floor(doubles[i] * num_tilings);
            base[i] = 0;
        }

        /*compute the tile numbers */
        for (j = 0; j < num_tilings; j++)
        {

            /* loop over each relevant dimension */
            for (i = 0; i < num_doubles; i++)
            {

                /* find coordinates of activated tile in tiling space */
                coordinates[i] = qstate[i] - mod(qstate[i]-base[i],num_tilings);

                /* compute displacement of next tiling in quantized space */
                base[i] += 1 + (2 * i);
            }
            /* add additional indices for tiling and hashing_set so they hash differently */
            coordinates[i] = j;

            tiles[j] = hash_UNH(coordinates, num_coordinates, memory_size, 449);
        }
        return;
    }



    /**
     *  hash_UNH
     *  Takes an array of integers and returns the corresponding tile after hashing 
     */
    static long[]  rndseq     = new long[2048];
    static boolean first_call =  true;
    private int hash_UNH(int[] ints, int num_ints, long m, int increment)
    {
        int i,k;
        long index;
        long sum = 0;

        /* if first call to hashing, initialize table of random numbers */
        if (first_call)
        {
            for (k = 0; k < 2048; k++)
            {
                rndseq[k] = 0;
                for (i=0; i < /*(int)sizeof(int)*/rndseq.length; ++i)
                    rndseq[k] = (rndseq[k] << 8) | ((int)(/*Math.random()*/Rand.INSTANCE.nextDouble()*Integer.MAX_VALUE) & 0xff);
            }
            first_call = false;
        }

        for (i = 0; i < num_ints; i++)
        {
            /* add random table offset for this dimension and wrap around */
            index = ints[i];
            index += (increment * i);
            index %= 2048;
            while (index < 0)
                index += 2048;

            /* add selected random number to sum */
            sum += (long)rndseq[(int)index];
        }
        index = (int)(sum % m);
        while (index < 0)
            index += m;

        return (int)index;
    }
    
    private static int[]   i_tmp_arr = new int[MAX_NUM_VARS];
//    private static double[] f_tmp_arr = new double[MAX_NUM_VARS];
    // no ints
    private void GetTiles(int tiles[],int nt,int memory,double doubles[],int nf)
    {
        GetTiles(tiles,nt,memory,doubles,nf,i_tmp_arr,0);
    }

/*
    // one int
    private void GetTiles(int tiles[],int nt,int memory,double doubles[],int nf,int h1)
    {
        i_tmp_arr[0]=h1;
        GetTiles(tiles,nt,memory,doubles,nf,i_tmp_arr,1);
    }

    // two ints
    private void GetTiles(int tiles[],int nt,int memory,double doubles[],int nf,int h1,int h2)
    {
        i_tmp_arr[0]=h1;
        i_tmp_arr[1]=h2;
        GetTiles(tiles,nt,memory,doubles,nf,i_tmp_arr,2);
    }

    // three ints
    private void GetTiles(int tiles[],int nt,int memory,double doubles[],int nf,int h1,int h2,int h3)
    {
        i_tmp_arr[0]=h1;
        i_tmp_arr[1]=h2;
        i_tmp_arr[2]=h3;
        GetTiles(tiles,nt,memory,doubles,nf,i_tmp_arr,3);
    }

    // one double, no ints
    private void GetTiles1(int tiles[],int nt,int memory,double f1)
    {
        f_tmp_arr[0]=f1;
        GetTiles(tiles,nt,memory,f_tmp_arr,1,i_tmp_arr,0);
    }

    // one double, one int
    private void GetTiles1(int tiles[],int nt,int memory,double f1,int h1)
    {
        f_tmp_arr[0]=f1;
        i_tmp_arr[0]=h1;
        GetTiles(tiles,nt,memory,f_tmp_arr,1,i_tmp_arr,1);
    }

    // one double, two ints
    private void GetTiles1(int tiles[],int nt,int memory,double f1,int h1,int h2)
    {
        f_tmp_arr[0]=f1;
        i_tmp_arr[0]=h1;
        i_tmp_arr[1]=h2;
        GetTiles(tiles,nt,memory,f_tmp_arr,1,i_tmp_arr,2);
    }

    // one double, three ints
    private void GetTiles1(int tiles[],int nt,int memory,double f1,int h1,int h2,int h3)
    {
        f_tmp_arr[0]=f1;
        i_tmp_arr[0]=h1;
        i_tmp_arr[1]=h2;
        i_tmp_arr[2]=h3;
        GetTiles(tiles,nt,memory,f_tmp_arr,1,i_tmp_arr,3);
    }

    // two doubles, no ints
    private void GetTiles2(int tiles[],int nt,int memory,double f1,double f2)
    {
        f_tmp_arr[0]=f1;
        f_tmp_arr[1]=f2;
        GetTiles(tiles,nt,memory,f_tmp_arr,2,i_tmp_arr,0);
    }

    // two doubles, one int
    private void GetTiles2(int tiles[],int nt,int memory,double f1,double f2,int h1)
    {
        f_tmp_arr[0]=f1;
        f_tmp_arr[1]=f2;
        i_tmp_arr[0]=h1;
        GetTiles(tiles,nt,memory,f_tmp_arr,2,i_tmp_arr,1);
    }

    // two doubles, two ints
    private void GetTiles2(int tiles[],int nt,int memory,double f1,double f2,int h1,int h2)
    {
        f_tmp_arr[0]=f1;
        f_tmp_arr[1]=f2;
        i_tmp_arr[0]=h1;
        i_tmp_arr[1]=h2;
        GetTiles(tiles,nt,memory,f_tmp_arr,2,i_tmp_arr,2);
    }

    // two doubles, three ints
    private void GetTiles2(int tiles[],int nt,int memory,double f1,double f2,int h1,int h2,int h3)
    {
        f_tmp_arr[0]=f1;
        f_tmp_arr[1]=f2;
        i_tmp_arr[0]=h1;
        i_tmp_arr[1]=h2;
        i_tmp_arr[2]=h3;
        GetTiles(tiles,nt,memory,f_tmp_arr,2,i_tmp_arr,3);
    }*/



    void GetTilesWrap(
	    int tiles[],               // provided array contains returned tiles (tile indices)
	    int num_tilings,           // number of tile indices to be returned in tiles       
        int memory_size,           // total number of possible tiles
	    double doubles[],            // array of doubleing point variables
        int num_doubles,            // number of doubleing point variables
        int wrap_widths[],         // array of widths (length and units as in doubles)
        int ints[],				   // array of integer variables
        int num_ints)              // number of integer variables
    {
        int i,j;
        int[] qstate = new int[MAX_NUM_VARS];
        int[] base   = new int[MAX_NUM_VARS];
        int[] wrap_widths_times_num_tilings = new int[MAX_NUM_VARS];
        int[] coordinates = new int[MAX_NUM_VARS * 2 + 1];   /* one interval number per relevant dimension */
        int num_coordinates = num_doubles + num_ints + 1;

        for (i = 0; i<num_ints; i++)
            coordinates[num_doubles+1+i] = ints[i];

        /* quantize state to integers (henceforth, tile widths == num_tilings) */
        for (i = 0; i < num_doubles; i++)
        {
            qstate[i] = (int) Math.floor(doubles[i] * num_tilings);
            base[i] = 0;
            wrap_widths_times_num_tilings[i] = wrap_widths[i] * num_tilings;
        }

        /*compute the tile numbers */
        for (j = 0; j < num_tilings; j++)
        {

            /* loop over each relevant dimension */
            for (i = 0; i < num_doubles; i++)
            {

                /* find coordinates of activated tile in tiling space */
                coordinates[i] = qstate[i] - mod(qstate[i]-base[i],num_tilings);
                if (wrap_widths[i]!=0)
                    coordinates[i] = coordinates[i] % wrap_widths_times_num_tilings[i];
                /* compute displacement of next tiling in quantized space */
                base[i] += 1 + (2 * i);
            }
            /* add additional indices for tiling and hashing_set so they hash differently */
            coordinates[i] = j;

            tiles[j] = hash_UNH(coordinates, num_coordinates, memory_size, 449);
        }
        return;
    }

    // no ints
    void GetTilesWrap(int tiles[],int num_tilings,int memory_size,double doubles[],       
        int num_doubles,int wrap_widths[])
    {
        GetTilesWrap(tiles,num_tilings,memory_size,doubles,
                num_doubles,wrap_widths,i_tmp_arr,0);
    }
}
