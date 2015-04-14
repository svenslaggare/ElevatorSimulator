/**
 * 
 */
package marl.utility;

/**
 * @author pds
 * @since  2013-07-04
 *
 */
public interface Compass<E> {
    
    /**
     * @return The angle in degrees of compass direction
     */
    double getAngle();
    /**
     * @return The opposite compass direction
     */
    E getOpposite();
    /**
     * @return The compass direction that is one step anti-clockwise
     */
    E getAntiClockwise();
    /**
     * @return The compass direction that is one step clockwise
     */
    E getClockwise();
    
    public static enum Cardinal implements Compass<Cardinal> {
        NORTH(0), EAST(90), SOUTH(180), WEST(270);
        
        private double angle_;
        private Cardinal(int angle) {
            angle_ = angle;
        }
        
        @Override
        public double getAngle() {
            return angle_;
        }
        
        @Override
        public Cardinal getOpposite() {
            switch( this ) {
                case NORTH:     return SOUTH;
                case EAST:      return WEST;
                case SOUTH:     return NORTH;
                case WEST:      return EAST;
                default:
                    throw new NullPointerException();
            }
        }
        @Override
        public Cardinal getAntiClockwise() {
            switch( this ) {
                case NORTH:     return WEST;
                case EAST:      return NORTH;
                case SOUTH:     return EAST;
                case WEST:      return SOUTH;
                default:
                    throw new NullPointerException();
            }
        }
        @Override
        public Cardinal getClockwise() {
            switch( this ) {
                case NORTH:     return EAST;
                case EAST:      return SOUTH;
                case SOUTH:     return WEST;
                case WEST:      return NORTH;
                default:
                    throw new NullPointerException();
            }
        }
    }
    public static enum Ordinal implements Compass<Ordinal> {
        NORTH(0), NORTHEAST(45), EAST(90), SOUTHEAST(135), SOUTH(180), SOUTHWEST(225), WEST(270), NORTHWEST(315);
        
        private double angle_;
        private Ordinal(int angle) {
            angle_ = angle;
        }
        
        @Override
        public double getAngle() {
            return angle_;
        }
        
        @Override
        public Ordinal getOpposite() {
            switch( this ) {
                case NORTH:     return SOUTH;
                case NORTHEAST: return SOUTHWEST;
                case EAST:      return WEST;
                case SOUTHEAST: return NORTHWEST;
                case SOUTH:     return NORTH;
                case SOUTHWEST: return NORTHEAST;
                case WEST:      return EAST;
                case NORTHWEST: return SOUTHEAST;
                default:
                    throw new NullPointerException();
            }
        }
        @Override
        public Ordinal getAntiClockwise() {
            switch( this ) {
                case NORTH:     return NORTHWEST;
                case NORTHEAST: return NORTH;
                case EAST:      return NORTHEAST;
                case SOUTHEAST: return EAST;
                case SOUTH:     return SOUTHEAST;
                case SOUTHWEST: return SOUTH;
                case WEST:      return SOUTHWEST;
                case NORTHWEST: return WEST;
                default:
                    throw new NullPointerException();
            }
        }
        @Override
        public Ordinal getClockwise() {
            switch( this ) {
                case NORTH:     return NORTHEAST;
                case NORTHEAST: return EAST;
                case EAST:      return SOUTHEAST;
                case SOUTHEAST: return SOUTH;
                case SOUTH:     return SOUTHWEST;
                case SOUTHWEST: return WEST;
                case WEST:      return NORTHWEST;
                case NORTHWEST: return NORTH;
                default:
                    throw new NullPointerException();
            }
        }
    }
    public static enum All implements Compass<All> {
        N(000.0d), NNE(022.5d), NE(045.0d), ENE(067.5d),
        E(090.0d), ESE(112.5d), SE(135.0d), SSE(157.5d),
        S(180.0d), SSW(202.5d), SW(225.0d), WSW(248.5d),
        W(270.0d), WNW(292.5d), NW(315.0d), NNW(337.0d);
        
        private double angle_;
        private All(double angle) {
            angle_ = angle;
        }
        
        @Override
        public double getAngle() {
            return angle_;
        }
        
        @Override
        public All getOpposite() {
            switch( this ) {
                case N:   return S;
                case NNE: return SSW;
                case NE:  return SW;
                case ENE: return WSW;
                case E:   return W;
                case ESE: return WNW;
                case SE:  return NW;
                case SSE: return NNW;
                case S:   return N;
                case SSW: return NNE;
                case SW:  return NE;
                case WSW: return ENE;
                case W:   return E;
                case WNW: return ESE;
                case NW:  return SE;
                case NNW: return SSE;
                default:
                    throw new NullPointerException();
            }
        }
        @Override
        public All getAntiClockwise() {
            switch( this ) {
                case N:   return NNE;
                case NNE: return NE;
                case NE:  return ENE;
                case ENE: return E;
                case E:   return ESE;
                case ESE: return SE;
                case SE:  return SSE;
                case SSE: return S;
                case S:   return SSW;
                case SSW: return SW;
                case SW:  return WSW;
                case WSW: return W;
                case W:   return WNW;
                case WNW: return NW;
                case NW:  return NNW;
                case NNW: return N;
                default:
                    throw new NullPointerException();
            }           
        }
        @Override
        public All getClockwise() {
            switch( this ) {
                case N:   return NNW;
                case NNE: return N;
                case NE:  return NNE;
                case ENE: return NE;
                case E:   return ENE;
                case ESE: return E;
                case SE:  return ESE;
                case SSE: return SE;
                case S:   return SSE;
                case SSW: return S;
                case SW:  return SSW;
                case WSW: return SW;
                case W:   return WSW;
                case WNW: return W;
                case NW:  return WNW;
                case NNW: return NW;
                default:
                    throw new NullPointerException();
            }  
        }
    }
}
