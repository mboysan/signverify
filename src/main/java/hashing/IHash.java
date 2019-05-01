package hashing;

public interface IHash {
    Object generateHash(String event) throws Exception;
    IHash mergeAndCreateNewHash(IHash hashToMerge) throws Exception;

    void setPosition(Position position);
    Position getPosition();

    enum Position {
        LEFT, RIGHT
    }
}
