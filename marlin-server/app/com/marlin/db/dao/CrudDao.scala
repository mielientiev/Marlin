package com.marlin.db.dao

import scala.concurrent.Future

/**
  * @author ntviet18@gmail.com
  */
trait CrudDao[T] {

  def save(t: T): Future[T]

  def findById(id: String): Future[Option[T]]

  def findAll(): Future[Seq[T]]

  def remove(id: String): Future[Unit]
}
