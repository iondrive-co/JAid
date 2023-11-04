package jaid.collection;

public interface IVector {

    double dotProduct(final IVector comparedTo);

    <T extends IVector> T minus(T operand);

    <T extends IVector> T normalize();

    int simHash();

    String toString();
}
