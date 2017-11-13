COMP416 - Computer Networks Project 1

To send bigger files try to read the object with a smaller buffer "while ((in.read(byte[1024])) != -1)" rather than Files.readFully because of OutOfMemoryException (I did not test these for files bigger than 300 MB but it should give OutOfMemoryException)
