package marl.observations;

public interface PullObserver extends Runnable {
    
    void pull(Observation observation);
    
}
