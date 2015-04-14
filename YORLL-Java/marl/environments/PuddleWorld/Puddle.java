/**
 * 
 */
package marl.environments.PuddleWorld;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public abstract class Puddle {
    /**
     * The maximum fine that can be received for being in a Puddle.
     */
    public static final double FINE = -400.0d;
    
    /**
     * Calculates the distance from the Puddle and uses that measure to generate
     * a fine for being in the Puddle.
     * @param state The position to be calculate the fine of
     * @return The calculated fine
     */
    public abstract double calcFine(PuddleWorldState state);
    /**
     * @return The radius of the puddle
     */
    public abstract double getRadius();
    
    
    public static class Line extends Puddle {
        /**
         * The lines that make up the Line Puddle.
         */
        private Line2D.Double[] parts_;
        /**
         * The radius of the lines.
         */
        private double          radius_;
        
        
        /**
         * @param radius The radius of the Puddle
         * @param x1     The first x coordinate
         * @param y1     The first y coordinate
         * @param x2     The second x coordinate
         * @param y2     The second y coordinate
         */
        public Line(double radius, double x1, double y1, double x2, double y2) {
            parts_  = new Line2D.Double[1];
            radius_ = radius;
            
            parts_[0] = new Line2D.Double(x1,y1,x2,y2);
        }
        /**
         * @param numLines The number of lines that make up this puddle
         * @param coords   An array of coordinates in the
         *                 form [P1x1,P1y1,P1x2,P1y2,P1x1,...]
         * @param radius   The radius of the Line Puddles
         */
        public Line(int numLines, double[] coords, double radius) {
            parts_  = new Line2D.Double[numLines];
            radius_ = radius;
            
            for( int i=0; i<numLines; i++ )
                parts_[i]
                        = new Line2D.Double(
                                coords[(4*i)+0],
                                coords[(4*i)+1],
                                coords[(4*i)+2],
                                coords[(4*i)+3]);
        }
        
        
        @Override
        public double calcFine(PuddleWorldState state) {
            double reward = 0.0;;
            for( int i=0; i<parts_.length; i++ ) {
                double dist = parts_[i].ptSegDist(state.getXPosition(), state.getYPosition());
                
                dist = (double)Math.round(dist * 20) / 20;
                if( dist < radius_ )
                    reward += FINE * (radius_ - dist);
            }
            
            return reward;
        }
        @Override
        public double getRadius() {
            // TODO Auto-generated method stub
            return radius_;
        }
        /**
         * @return The number of lines that make up the Line Puddle
         */
        public int getNumLines() {
            return parts_.length;
        }
        /**
         * @param what Which line to return
         * @return The Line of the specified number
         */
        public Line2D.Double getPart(int what) {
            return parts_[what];
        }
    }
    public static class Poly extends Puddle {
        /**
         * A description of the Shape of the puddle, also will be used to do
         * the mathematics in calculating the fine.
         */
        private Path2D.Double    shape_;
        /**
         * The lines that make up the outer edge of the Shape of this Puddle.
         */
        private Line2D.Double[]  lines_;
        /**
         * The radius of the Puddle extending beyond the edges of the Puddle.
         */
        private double           radius_;
        /**
         * @param coords All the points of the Polygon
         *               An array in the form [x1,y1,x2,y2,...]
         */
        public Poly(double[] coords, double radius) {
            int numPoints = coords.length / 2;
            if( numPoints <= 2 )
                throw new RuntimeException("A Polygon needs at least 3 points");

            shape_  = new Path2D.Double();
            lines_  = new Line2D.Double[numPoints];
            radius_ = radius;
            
            shape_.moveTo(coords[0], coords[1]);
            for( int i=0; i<numPoints; i++ ) {
                shape_.lineTo(coords[(2*i)+0], coords[(2*i)+1]);
                if( i > 0 )
                    lines_[i] = new Line2D.Double(
                            coords[(2*i)-2], coords[(2*i)-1],
                            coords[(2*i)+0], coords[(2*i)+1]);
            }
            
            // close the paths
            shape_.closePath();
            lines_[0] = new Line2D.Double(
                    coords[(2*0)+0], coords[(2*0)+1],
                    coords[(2*numPoints)-2], coords[(2*numPoints)-1]);
                
        }

        
        @Override
        public double calcFine(PuddleWorldState state) {
            double reward = 0.0;
            
            if( shape_.contains(state.getXPosition(), state.getYPosition()) )
                reward = FINE * radius_;
            else {
                for( int i=0; i<lines_.length; i++ ) {
                    double dist = lines_[i].ptSegDist(state.getXPosition(), state.getYPosition());
                    
                    dist = (double)Math.round(dist * 20) / 20;
                    if( dist < radius_ )
                        reward += FINE * (radius_ - dist);
                }
            }
            
            return reward;
        }
        @Override
        public double getRadius() {
            return radius_;
        }
        
        public Path2D.Double getShape() {
            return shape_;
        }
        /**
         * @param what Which line to return
         * @return The Line of the specified number
         */
        public Line2D.Double getLine(int what) {
            if( what >= lines_.length )
                return null;
            else
                return lines_[what];
        }
    }
}
