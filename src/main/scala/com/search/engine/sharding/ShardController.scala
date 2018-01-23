package com.search.engine.sharding

import com.search.engine.model.{Document, Key}

import scala.concurrent.Future

trait ShardController {

  def put(key: Key, document: Document): Future[Unit]

  def search(query: String): Future[Seq[Key]]
}


