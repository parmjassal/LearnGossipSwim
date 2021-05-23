# LearnGossipSwim
Learn gossip based on https://www.cs.cornell.edu/projects/Quicksilver/public_pdfs/SWIM.pdf

Tried to hack SWIM gossip protocol after going through
https://www.cs.cornell.edu/projects/Quicksilver/public_pdfs/SWIM.pdf

1. Used the same PING message to send alive message piggybacking the ping only.
2. Incrementing the self incarnation id on recieving self suspect message.
3. Using config based retransmission instead of Constant*log(n)
4. As part of PING_REQ, forwarding member will not update it's state.


Missing parts:-
1. Move to Suspect after retry with k members.
2. Batching of events is missing when piggybacking on PING.
3. Schuffle is missing after first round.
