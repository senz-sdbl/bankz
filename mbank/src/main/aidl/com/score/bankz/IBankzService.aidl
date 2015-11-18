package com.score.bankz;

import com.score.senzc.pojos.Senz;

interface IBankzService {
    // send senz messages to service via this function
    void send(in Senz senz);
}
