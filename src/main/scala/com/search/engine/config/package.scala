package com.search.engine

import akka.http.scaladsl.model.Uri
import com.typesafe.config.Config

import scala.collection.JavaConversions._

package object config {

  implicit def distinguishedUri(implicit config: Config): Uri = {
    val distConfig = config.getConfig("search-server.distinguished-node")
    Uri(s"http://${distConfig.getString("host")}:${distConfig.getString("port")}")
  }

  implicit def shardUris(implicit config: Config): Seq[Uri] = {
    val shards = config.getObjectList("search-server.shard-nodes")
    shards.map {
      obj =>
        val shardConf = obj.toConfig
        Uri(s"http://${shardConf.getString("host")}:${shardConf.getString("port")}")
    }
  }
}
