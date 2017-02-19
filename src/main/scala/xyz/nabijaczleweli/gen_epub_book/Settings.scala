package xyz.nabijaczleweli.gen_epub_book

import java.io.File

class Settings(args: Array[String]) {
	assert(!(args.length < 2), "Not enough arguments, required: infile, outfile")
	assert(!(args.length > 2), "Too many arguments, required: infile, outfile")

	val inFile = new File(args(0))
	val outFile = new File(args(1))

	if(!inFile.exists || !inFile.isFile)
		throw new RuntimeException(s"Input file ${args(0)} nonexistant")
}
