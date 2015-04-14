/**
 * 
 */
package marl.environments.CartPole;

import marl.ext.tilecoding.TileCodingState;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public class CartPoleState implements TileCodingState<CartPoleState> {
    
    /**
     * The location of the Cart.
     */
    private double cartLocation_;
    /**
     * The velocity of the Cart.
     */
    private double cartVelocity_;
    /**
     * The angle of the pole on top of the Cart.
     */
    private double poleAngle_;
    /**
     * The velocity of the pol eon top of the Cart.
     */
    private double poleVelocity_;
    
    /**
     * 
     */
    public CartPoleState() {
        set(0.0d, 0.0d, 0.0d, 0.0d);
    }
    public CartPoleState(double cartLocation, double cartVelocity, double poleAngle, double poleVelocity) {
        set(cartLocation, cartVelocity, poleAngle, poleVelocity);
    }
    public CartPoleState(CartPoleState that) {
        set(that.cartLocation_, that.cartVelocity_, that.poleAngle_, that.poleVelocity_);
    }
    
    
    public void set(double cartLocation, double cartVelocity, double poleAngle, double poleVelocity) {
        cartLocation_ = cartLocation;
        cartVelocity_ = cartVelocity;
        poleAngle_    = poleAngle;
        poleVelocity_ = poleVelocity;
    }
    /* (non-Javadoc)
     * @see marl.environments.State#set(marl.environments.State)
     */
    @Override
    public void set(CartPoleState that) {
        set(that.cartLocation_, that.cartVelocity_, that.poleAngle_, that.poleVelocity_);
    }



    
    /**
     * @return the cartLocation
     */
    public double getCartLocation() {
        return cartLocation_;
    }
    /**
     * @param cartLocation the cartLocation to set
     */
    public void setCartLocation(double cartLocation) {
        cartLocation_ = cartLocation;
    }
    /**
     * @return the cartVelocity
     */
    public double getCartVelocity() {
        return cartVelocity_;
    }
    /**
     * @param cartVelocity the cartVelocity to set
     */
    public void setCartVelocity(double cartVelocity) {
        cartVelocity_ = cartVelocity;
    }


    
    /**
     * @return the poleAngle
     */
    public double getPoleAngle() {
        return poleAngle_;
    }
    /**
     * @param poleAngle the poleAngle to set
     */
    public void setPoleAngle(double poleAngle) {
        poleAngle_ = poleAngle;
    }
    /**
     * @return the poleVelocity
     */
    public double getPoleVelocity() {
        return poleVelocity_;
    }
    /**
     * @param poleVelocity the poleVelocity to set
     */
    public void setPoleVelocity(double poleVelocity) {
        poleVelocity_ = poleVelocity;
    }
    
    
    
    // Tile Coding State extras
    @Override
    public double getFeature(int featureNo) {
        switch( featureNo ) {
            case 0:  return cartLocation_;
            case 1:  return cartVelocity_;
            case 2:  return poleAngle_;
            default: return poleVelocity_;
        }
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof CartPoleState ) {
            CartPoleState that = (CartPoleState)obj;
            return  this.cartLocation_ == that.cartLocation_  &&
                    this.cartVelocity_ == that.cartVelocity_  &&
                    this.poleAngle_    == that.poleAngle_     &&
                    this.poleVelocity_ == that.poleVelocity_;
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "CartPoleState[cartLocation="+cartLocation_+", cartVelocity="+cartVelocity_+
                ", poleAngle="+poleAngle_+", poleVelocity="+poleVelocity_+"]";
    }
}
