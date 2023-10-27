package jaid.collection;

public interface IVector {

    double dotProduct(final IVector comparedTo);

    int simHash();

    String toString();
}
