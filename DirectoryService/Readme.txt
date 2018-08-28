1. The hash keys identifying the stored contents are specified from requests instead generated from contents

2. Memory-based storage in Chord, no persistency

3. TCP-based socket communication, around 200-250ms communication setup delay

4. Connection timeout is configured in the SockProxy class

5. set de.uniba.wiai.lspi.util.logging.off to true in chord.properties for logging 