package org.kunicki.monad_transformers

import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long, name: String)

case class Company(id: Long, name: String)

class FromScratch:

  type Effect[A] = OptionInsideFuture[A]

  private def findUserById(id: Long): Effect[User] = ???

  private def findCompanyByUser(user: User): Effect[Company] = ???

  def findCompanyByUserId(id: Long)(using ExecutionContext): Effect[Company] =
    for {
      user <- findUserById(id)
      company <- findCompanyByUser(user)
    } yield company

case class OptionInsideFuture[A](value: Future[Option[A]]):

  def map[B](f: A => B)(using ExecutionContext): OptionInsideFuture[B] =
    OptionInsideFuture(value.map(_.map(f)))

  def flatMap[B](f: A => OptionInsideFuture[B])(using ExecutionContext): OptionInsideFuture[B] =
    OptionInsideFuture(
      value.flatMap {
        case Some(a) => f(a).value
        case None => Future.successful(None)
      }
    )
