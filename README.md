## Description

This project is created to check the integrity of any regular file (e.g. a log file) with a signature created using
binary hash trees (a.k.a Merkle Tree).

Using the command line interface, one can easily sign a file and create a signature based on the file content. 
Then, the same file (or another with the same content) and the produced signature can be used to check if the file
is modified or not.

## Getting Started

Clone the repository and run:

```bash
mvn clean install

# lets' rename the created jar file
cd target
mv signverify-1.0-SNAPSHOT-jar-with-dependencies.jar signverify.jar

# print the help instructions
java -jar signverify.jar --help
```

Create a non-empty log file in the ```./target``` directory called ```testlog.txt```. And change the working directory
to target: ```cd ./target```.

Following are basic usage of the application.

#### Sign and Verify

A simple example of signing and verifying a log file.

```
# sign the testlog.txt file and specify the signature file output.
java -jar signverify.jar sign ./testlog.txt ./signature.sig

# verify the integrity of the testlog.txt by providing 
# the previously produced signature file
java -jar signverify.jar verify ./testlog.txt ./signature.sig
```

You can try verifying the file after changing its contents. It should fail.

#### Extract Hash Chain

You can verify if the log file contains a certain line/event by extracting the hash chain from the line to the 
complete hash of the whole file. This is also called generating the audit proof.

Following is an example:

```
# produce the hash chain of an event to calculate the root hash.
java -jar signverify.jar hashchain ./testlog.txt "event to test"
```

#### Visualize Tree

**Note:** This is currently experimental only.

You can also visualize the complete hash tree of a file.

```
# produce the hash chain of an event to calculate the root hash.
java -jar signverify.jar visualize ./testlog.txt
```

## Detailed Usage

The commands explained above have different parameters to use which might come in handy for different usage scenarios.

Keep in mind that you can display usage information for each command by running:

```
# base usage info:
java -jar signverify.jar --help

# usage info for any command:
java -jar signverify.jar {sign,verify,hashchain,visualize} --help

# example
java -jar signverify.jar sign --help
```

#### Sign Command

There are a couple of options for the sign command:

* **Changing the hash algorithm:** The default algorithm used for hashing is ```SHA-256```. You can change the hashing 
algorithm used with one of the possible options in ```{SHA-256, SHA-1, MD5}```. Example:
```
java -jar signverify.jar sign ./testlog.txt ./signature.sig --hash-algorithm MD5
```

* **Specifying if the Log file is static or append-only:** By default, any changes made to the signed file will
result in verification failures. However, when signing the log file, you can allow appends made to the log file
(e.g. usage: append-only log files). Example:
```
java -jar signverify.jar sign ./testlog.txt ./signature.sig --allow-append true
```

**Note:** Any special option specified when signing the file will be persisted in the signature file produced.

#### Verify Command

The verify command has no special option. The only requirement is to provide the log file signed and the signature file 
produced when signing the document. Example:
```
java -jar signverify.jar verify ./testlog.txt ./signature.sig
```

#### Hashchain Command

A couple of different options exist for this command.

* **Chaning the hash algorithm for producing hash chain:** By default, ```SHA-256``` hashing algorithm is used for 
generating the hash chain. You can change this with one of the options in ```{SHA-256, SHA-1, MD5}```. Example:
```
java -jar signverify.jar hashchain ./testlog.txt "event to test" --hash-algorithm MD5
```

* **Outputting the chain to a file:** For some scenarios, you may want to persist hash chain in a file. For this, you
can use the ```-out``` option. The contents of the file is serialized ```IHash``` objects that can be read with the 
application's API.
```
java -jar signverify.jar hashchain ./testlog.txt "event to test" -out MD5
```

#### Visualize Command

Options available for the command:

* **Chaning the hash algorithm for hash tree formation:** By default, ```SHA-256``` hashing algorithm is used for 
generating the hash tree. You can change this with one of the options in ```{SHA-256, SHA-1, MD5}```. Example:
```
java -jar signverify.jar visualize ./testlog.txt --hash-algorithm MD5
```

* **Writing the output to file:** You can write the created visualization to a file.
```
java -jar signverify.jar visualize ./testlog.txt -out ./visaulized.txt
```

* **Specifying the hash length:** You can specify the hash string length per each node as well.
```
java -jar signverify.jar visualize ./testlog.txt -hl 6
```

### Operation Mode

When using any of the commands abobe, by default, the program builds the internal hash tree (merkle tree) in a memory 
intensive manner, meaning the nodes are stored in java Hash maps for faster verification. However there is also an 
option for running the operations above in a CPU intensive manner. For this, you can specify the ```-opmod``` option 
like:
```
java -jar signverify.jar -opmod CPU verify ./testlog.txt ./signature.sig
```

## Using the API

You can use this project as a library as well with its useful API.

Take a look at the [SignVerify](https://github.com/mboysan/guardtime-assignment/blob/master/src/main/java/ops/SignVerify.java)
class for the available API operations.

For some examples/samples, you can also checkout the [SignVerifyTest](https://github.com/mboysan/guardtime-assignment/blob/master/src/test/java/ops/SignVerifyTest.java)
class.