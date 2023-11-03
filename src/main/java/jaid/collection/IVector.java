package jaid.collection;

public interface IVector {

    double dotProduct(final IVector comparedTo);

    <T extends IVector> T minus(T operand);

    int simHash();

    String toString();
}
