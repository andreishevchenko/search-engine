package com.search.engine.store

import com.search.engine.model.{Document, Key}

import scala.concurrent.Future

trait DocumentStore {

  def put(key: Key, document: Document): Future[Unit]

  def get(key: Key): Future[Document]
}