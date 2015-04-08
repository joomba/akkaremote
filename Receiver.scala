package sample.remote.benchmark

import akka.actor.Actor
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props
case class Time(message: Long)

object Receiver {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Sys", ConfigFactory.load("remotelookup"))
    system.actorOf(Props[Receiver], "rcv")
  }
}

class Receiver extends Actor {
  import Sender._

  def receive = {
    //case m: Echo  => sender() ! m
    //case Shutdown => context.system.shutdown()
    case _        => {
        val time = System.currentTimeMillis
        println (s"Принято в $time")
        //sender () ! Done
        sender () ! Time(time)
    }
        
  }
}

