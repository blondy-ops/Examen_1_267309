package modelo;

public interface IObservable {
    void addObserver(IObservador o);
    void removeObserver(IObservador o);
    void notifyObservers();
}
