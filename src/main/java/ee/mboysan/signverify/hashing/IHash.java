package ee.mboysan.signverify.hashing;

import java.io.Serializable;

public interface IHash extends Serializable {
    Object generateHash(String event) throws Exception;
    IHash mergeAndCreateNewHash(IHash hashToMerge) throws Exception;

    void setPosition(Position position);
    Position getPosition();

    enum Position {
        LEFT, RIGHT
    }
}
