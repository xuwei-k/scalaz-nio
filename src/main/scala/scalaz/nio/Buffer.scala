package scalaz.nio

import java.nio.{
  Buffer => JBuffer,
  ByteBuffer => JByteBuffer,
  CharBuffer => JCharBuffer,
  FloatBuffer => JFloatBuffer,
  DoubleBuffer => JDoubleBuffer,
  IntBuffer => JIntBuffer,
  LongBuffer => JLongBuffer,
  ShortBuffer => JShortBuffer
}
import scalaz.zio.{ Chunk, IO, JustExceptions, UIO, ZIO }

import scala.reflect.ClassTag

@specialized // See if Specialized will work on return values, e.g. `get`
abstract class Buffer[A: ClassTag] private[nio] (private[nio] val buffer: JBuffer) {
  final val capacity: UIO[Int] = ZIO.succeed(buffer.capacity)

  final val position: UIO[Int] = IO.effectTotal(buffer.position)

  final def position(newPosition: Int): IO[Exception, Unit] =
    IO.effect(buffer.position(newPosition)).void.refineOrDie(JustExceptions)

  final val limit: UIO[Int] = IO.effectTotal(buffer.limit)

  final val remaining: UIO[Int] = IO.effectTotal(buffer.remaining)

  final val hasRemaining: UIO[Boolean] = IO.effectTotal(buffer.hasRemaining)

  final def limit(newLimit: Int): IO[Exception, Unit] =
    IO.effect(buffer.limit(newLimit)).void.refineOrDie(JustExceptions)

  final val mark: UIO[Unit] = IO.effectTotal(buffer.mark()).void

  final val reset: IO[Exception, Unit] =
    IO.effect(buffer.reset()).void.refineOrDie(JustExceptions)

  final val clear: UIO[Unit] = IO.effectTotal(buffer.clear()).void

  final val flip: UIO[Unit] = IO.effectTotal(buffer.flip()).void

  final val rewind: UIO[Unit] = IO.effectTotal(buffer.rewind()).void

  final val isReadOnly: UIO[Boolean] = IO.succeed(buffer.isReadOnly)

  final val hasArray: UIO[Boolean] = IO.succeed(buffer.hasArray)

  final def arrayOffset: IO[Exception, Int] =
    IO.effect(buffer.arrayOffset).refineOrDie(JustExceptions)

  final val isDirect: UIO[Boolean] = IO.succeed(buffer.isDirect)

  val array: IO[Exception, Array[A]]

  val get: IO[Exception, A]

  def get(i: Int): IO[Exception, A]

  def put(element: A): IO[Exception, Buffer[A]]

  def put(index: Int, element: A): IO[Exception, Buffer[A]]

  val asReadOnlyBuffer: IO[Exception, Buffer[A]]

}

object Buffer {

  def byte(capacity: Int): IO[Exception, Buffer[Byte]] =
    IO.effect(JByteBuffer.allocate(capacity)).map(new ByteBuffer(_)).refineOrDie(JustExceptions)

  def byte(chunk: Chunk[Byte]): IO[Exception, Buffer[Byte]] =
    IO.effect(JByteBuffer.wrap(chunk.toArray)).map(new ByteBuffer(_)).refineOrDie(JustExceptions)

  def char(capacity: Int): IO[Exception, Buffer[Char]] =
    IO.effect(JCharBuffer.allocate(capacity)).map(new CharBuffer(_)).refineOrDie(JustExceptions)

  def char(chunk: Chunk[Char]): IO[Exception, Buffer[Char]] =
    IO.effect(JCharBuffer.wrap(chunk.toArray)).map(new CharBuffer(_)).refineOrDie(JustExceptions)

  def float(capacity: Int): IO[Exception, Buffer[Float]] =
    IO.effect(JFloatBuffer.allocate(capacity)).map(new FloatBuffer(_)).refineOrDie(JustExceptions)

  def float(chunk: Chunk[Float]): IO[Exception, Buffer[Float]] =
    IO.effect(JFloatBuffer.wrap(chunk.toArray)).map(new FloatBuffer(_)).refineOrDie(JustExceptions)

  def double(capacity: Int): IO[Exception, Buffer[Double]] =
    IO.effect(JDoubleBuffer.allocate(capacity)).map(new DoubleBuffer(_)).refineOrDie(JustExceptions)

  def double(chunk: Chunk[Double]): IO[Exception, Buffer[Double]] =
    IO.effect(JDoubleBuffer.wrap(chunk.toArray))
      .map(new DoubleBuffer(_))
      .refineOrDie(JustExceptions)

  def int(capacity: Int): IO[Exception, Buffer[Int]] =
    IO.effect(JIntBuffer.allocate(capacity)).map(new IntBuffer(_)).refineOrDie(JustExceptions)

  def int(chunk: Chunk[Int]): IO[Exception, Buffer[Int]] =
    IO.effect(JIntBuffer.wrap(chunk.toArray)).map(new IntBuffer(_)).refineOrDie(JustExceptions)

  def long(capacity: Int): IO[Exception, Buffer[Long]] =
    IO.effect(JLongBuffer.allocate(capacity)).map(new LongBuffer(_)).refineOrDie(JustExceptions)

  def long(chunk: Chunk[Long]): IO[Exception, Buffer[Long]] =
    IO.effect(JLongBuffer.wrap(chunk.toArray)).map(new LongBuffer(_)).refineOrDie(JustExceptions)

  def short(capacity: Int): IO[Exception, Buffer[Short]] =
    IO.effect(JShortBuffer.allocate(capacity)).map(new ShortBuffer(_)).refineOrDie(JustExceptions)

  def short(chunk: Chunk[Short]): IO[Exception, Buffer[Short]] =
    IO.effect(JShortBuffer.wrap(chunk.toArray)).map(new ShortBuffer(_)).refineOrDie(JustExceptions)
}
