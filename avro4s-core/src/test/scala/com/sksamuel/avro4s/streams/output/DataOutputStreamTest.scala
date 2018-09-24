package com.sksamuel.avro4s.streams.output

import java.io.ByteArrayOutputStream

import com.sksamuel.avro4s.AvroOutputStream
import com.sksamuel.avro4s.internal.{AvroSchema, SchemaFor, Encoder}
import org.apache.avro.file.{DataFileReader, SeekableByteArrayInput}
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.scalatest.{FunSuite, Matchers}

trait DataOutputStreamTest extends FunSuite with Matchers {

  def read[T: SchemaFor](out: ByteArrayOutputStream): GenericRecord = read(out.toByteArray)
  def read[T: SchemaFor](bytes: Array[Byte]): GenericRecord = {
    val datumReader = new GenericDatumReader[GenericRecord](AvroSchema[T])
    val dataFileReader = new DataFileReader[GenericRecord](new SeekableByteArrayInput(bytes), datumReader)
    new Iterator[GenericRecord] {
      override def hasNext: Boolean = dataFileReader.hasNext
      override def next(): GenericRecord = dataFileReader.next
    }.toList.head
  }

  def write[T: Encoder : SchemaFor](t: T): ByteArrayOutputStream = {
    val schema = AvroSchema[T]
    val out = new ByteArrayOutputStream
    val avro = AvroOutputStream.data[T](out, schema)
    avro.write(t)
    avro.close()
    out
  }


  //  def readB[T](out: ByteArrayOutputStream, schema: Schema): GenericRecord = readB(out.toByteArray)
  //  def readB[T](bytes: Array[Byte], schema: Schema): GenericRecord = {
  //    val datumReader = new GenericDatumReader[GenericRecord](schema())
  //    val decoder = DecoderFactory.get().binaryDecoder(bytes, null)
  //    datumReader.read(null, decoder)
  //  }
}