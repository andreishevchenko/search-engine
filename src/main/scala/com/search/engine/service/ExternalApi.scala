package com.search.engine.service

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.search.engine.model.{Document, Key}
import com.search.engine.nodes.DistinguishedNodeControllerComponent

import scala.concurrent.ExecutionContext.Implicits.global

trait ExternalApi {
  this: DistinguishedNodeControllerComponent =>

  lazy val externalApiRoute: Route = documentRoute ~ searchRoute

  lazy val documentRoute: Route = path("document") {
    get {
      parameter('key.as[String]) {
        key =>
          onSuccess(distinguishedNodeController.get(Key(key))) {
            document =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, document.value))
          }
      }
    } ~
      post {
        parameters('key.as[String], 'document.as[String]) {
          (key, document) =>
            onSuccess(distinguishedNodeController.put(Key(key), Document(document))) {
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "ok"))
            }
        }
      }
  }

  lazy val searchRoute: Route = path("search") {
    get {
      parameter('search.as[String]) {
        search =>
          onSuccess(distinguishedNodeController.search(search)) {
            keys =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"keys: ${keys.map(_.value).mkString(",")}"))
          }
      }
    }
  }
}
