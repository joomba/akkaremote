package sample.remote.benchmark

import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorIdentity
import akka.actor.ActorRef
import akka.actor.Identify
import akka.actor.ReceiveTimeout
import akka.actor.Terminated
import akka.actor.Props
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import scala.io.Source

object Sender {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Sys", ConfigFactory.load("calculator"))

    val remoteHostPort = if (args.length >= 1) args(0) else "127.0.0.1:2553"
    val remotePath = s"akka.tcp://Sys@$remoteHostPort/user/rcv"
    val totalMessages = if (args.length >= 2) args(1).toInt else 500000
    val burstSize = if (args.length >= 3) args(2).toInt else 5000
    val payloadSize = if (args.length >= 4) args(3).toInt else 100

    system.actorOf(Sender.props(remotePath, totalMessages, burstSize, payloadSize), "snd")
  }

  def props(path: String, totalMessages: Int, burstSize: Int, payloadSize: Int): Props =
    Props(new Sender(path, totalMessages, burstSize, payloadSize))

  private case object Warmup
  case object Shutdown
  sealed trait Echo
  case object Start extends Echo
  case object Done
  case class Continue(remaining: Int, startTime: Long, burstStartTime: Long, n: Int)
    extends Echo
}

class Sender(path: String, totalMessages: Int, burstSize: Int, payloadSize: Int) extends Actor {
  import Sender._

  val payload: Array[Byte] = Vector.fill(payloadSize)("a").mkString.getBytes
  var startTime = 0L
  var maxRoundTripMillis = 0L

  context.setReceiveTimeout(3.seconds)
  sendIdentifyRequest()

  def sendIdentifyRequest(): Unit =
    context.actorSelection(path) ! Identify(path)

  def receive = identifying

  def identifying: Receive = {
    case ActorIdentity(`path`, Some(actor)) =>
      context.watch(actor)
      context.become(active(actor))
      context.setReceiveTimeout(Duration.Undefined)
      self ! Warmup
    case ActorIdentity(`path`, None) => println(s"Remote actor not available: $path")
    case ReceiveTimeout              => sendIdentifyRequest()
  }

  def active(actor: ActorRef): Receive = {
      
   // case Warmup =>
//      println(s"Начало отправки файлов удаленному actorу")
 //     actor ! Start
      
    
    case Done => {
        val receivetime=System.currentTimeMillis
        println (s"Принят отчет о принятии в $receivetime")
    }
    
    case Time(time)=>{
        println (s"Time")
        
        println (s"$time")
    }

    case Warmup =>
       println(s"Начало отправки файлов удаленному actorу")
      
       //Засечка времени отправки
       val sendtime=System.currentTimeMillis
       //Отправка короткого JSON-файла
       //Открытие файла
       val filename = "/Users/joomba/Downloads/diplom/middle.docx"
       val fileContents = Source.fromFile(filename).getLines.mkString
       println (s"Отправлено в $sendtime")
       actor ! fileContents
       
       //inbox.send(receiver, fileContents)
       
       // Получение и вывод ответного сообщения со временем получения файла
       //val Time(message1) = inbox.receive(1.seconds)
       //println(s"Time: $message1")
       
       //Время передачи сообщения
       //val timeOfSend = message1-sendtime
       //println (s"Время передачи сообщения (303 байта) составило: $timeOfSend мс")
  
      //actor ! "Hello"
      

  }

}

