package com.search.engine.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.{Done, pattern}
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import com.search.engine.nodes.{DistinguishedNodeController, DistinguishedNodeControllerComponent}
import com.search.engine.service.ExternalApi
import com.search.engine.sharding.{ShardControllerByHash, ShardNodeConnector, ShardNodeConnectorHttp}
import com.search.engine.store.DocumentStoreInMemory

import scala.concurrent.{ExecutionContext, Future}


class DistinguishedServer(shardUris: Seq[Uri], shutdownSignal: Future[Done]) extends HttpApp with ExternalApi with DistinguishedNodeControllerComponent {


  private[this] lazy val documentStore = new DocumentStoreInMemory
  private[this] lazy val shardController = new ShardControllerByHash(shardHttpConnectors(shardUris))(ExecutionContext.global)

  protected[this] lazy val distinguishedNodeController = new DistinguishedNodeController(documentStore, shardController)

  override def routes: Route = externalApiRoute

  override def waitForShutdownSignal(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext): Future[Done] = shutdownSignal

  private def shardHttpConnectors(uris: Seq[Uri]): Map[Int, ShardNodeConnector] = {
    implicit val clientSystem: ActorSystem = ActorSystem()
    implicit val clientMaterializer: ActorMaterializer = ActorMaterializer()
    uris
      .map(new ShardNodeConnectorHttp(_))
      .foldLeft(0 -> Map.empty[Int, ShardNodeConnector]) {
        case ((count, acc), connector) => count + 1 -> acc.+(count -> connector)
      }
      ._2
  }
}