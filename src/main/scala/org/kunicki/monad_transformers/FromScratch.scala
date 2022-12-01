package org.kunicki.monad_transformers

case class User(id: Long, name: String)

case class Company(id: Long, name: String)

class FromScratch:

  type Effect[A] = A

  private def findUserById(id: Long): Effect[User] = ???

  private def findCompanyByUser(user: User): Effect[Company] = ???

  def findCompanyByUserId(id: Long): Effect[Company] =
    findCompanyByUser(findUserById(id))
