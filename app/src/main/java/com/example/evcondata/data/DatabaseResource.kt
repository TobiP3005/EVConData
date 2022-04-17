package com.example.evcondata.data

import com.couchbase.lite.*

class DatabaseResource (var database: Database) {

    //replication
    var replicator: Replicator? = null
    var replicatorConfiguration: ReplicatorConfiguration? = null

}