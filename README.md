# Description

This project is created to check the integrity of any regular file (e.g. a log file) with a signature created using
binary hash trees (a.k.a Merkle Tree).

Using the command line interface, one can easily sign a file and create a signature based on the file content. 
Then, the same file (or another with the same content) and the produced signature can be used to check if the file
is modified or not.

# Getting Started

Clone the repository and run:

```bash
mvn clean install

# lets' rename the created jar file
cd target
mv signverify-1.0-SNAPSHOT-jar-with-dependencies.jar signverify.jar

# print the help instructions
java -jar signverify.jar --help
```

Assuming currnet working directory is ```./target```, create a non-empty log file ```testlog.txt```. You can also use 
a sample log file contained in main resources, [testlog.txt](https://github.com/mboysan/guardtime-assignment/blob/master/src/main/resources/testlog.txt).

Following are basic usage of the application.

#### Sign and Verify

A simple example of signing and verifying a log file.

```bash
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

```bash
# produce the hash chain of an event to calculate the root hash.
java -jar signverify.jar hashchain ./testlog.txt "event to test"
```

#### Visualize Tree (experimental)

You can also visualize the complete hash tree of a file.

```bash
# produce the hash chain of an event to calculate the root hash.
java -jar signverify.jar visualize ./testlog.txt
```

# Detailed Usage

The commands explained above have different parameters to use which might come in handy for different usage scenarios.

Keep in mind that you can display usage information for each command by running:

```bash
# base usage info:
java -jar signverify.jar --help

# usage info for any command:
java -jar signverify.jar {sign,verify,hashchain,visualize} --help

# example
java -jar signverify.jar sign --help
```

### Sign Command

There are a couple of options for the sign command:

* **Changing the hash algorithm:** The default algorithm used for hashing is ```SHA-256```. You can change the hashing 
algorithm used with one of the possible options in ```{SHA-256, SHA-1, MD5}```. Example:
```bash
java -jar signverify.jar sign ./testlog.txt ./signature.sig --hash-algorithm MD5
```

* **Specifying if the Log file is static or append-only:** By default, any changes made to the signed file will
result in verification failures. However, when signing the log file, you can allow appends made to the log file
(e.g. usage: append-only log files). Example:
```bash
java -jar signverify.jar sign ./testlog.txt ./signature.sig --allow-append true
```

**Note:** Any special option specified when signing the file will be persisted in the signature file produced.

### Verify Command

The verify command has no special option. The only requirement is to provide the log file signed and the signature file 
produced when signing the document. Example:
```bash
java -jar signverify.jar verify ./testlog.txt ./signature.sig
```

### Hashchain Command

A couple of different options exist for this command.

* **Chaning the hash algorithm for producing hash chain:** By default, ```SHA-256``` hashing algorithm is used for 
generating the hash chain. You can change this with one of the options in ```{SHA-256, SHA-1, MD5}```. Example:
```bash
java -jar signverify.jar hashchain ./testlog.txt "event to test" --hash-algorithm MD5
```

* **Outputting the chain to a file:** For some scenarios, you may want to persist hash chain in a file. For this, you
can use the ```-out``` option. The contents of the file is serialized ```IHash``` objects that can be read with the 
application's API.
```bash
java -jar signverify.jar hashchain ./testlog.txt "event to test" -out MD5
```

### Visualize Command

**NB!** This feature is currently experimental.

Options available for the command:

* **Chaning the hash algorithm for hash tree formation:** By default, ```SHA-256``` hashing algorithm is used for 
generating the hash tree. You can change this with one of the options in ```{SHA-256, SHA-1, MD5}```. Example:
```bash
java -jar signverify.jar visualize ./testlog.txt --hash-algorithm MD5
```

* **Writing the output to file:** You can write the created visualization to a file.
```bash
java -jar signverify.jar visualize ./testlog.txt -out ./visaulized.txt
```

* **Specifying the hash length:** You can specify the hash string length per each node as well.
```bash
java -jar signverify.jar visualize ./testlog.txt -hl 6
```

## Operation Mode

When using any of the commands abobe, by default, the program builds the internal hash tree (merkle tree) in a memory 
intensive manner, meaning the nodes are stored in java Hash maps for faster verification. However there is also an 
option for running the operations above in a CPU intensive manner. For this, you can specify the ```-opmod``` option 
like:
```bash
java -jar signverify.jar -opmod CPU verify ./testlog.txt ./signature.sig
```

# Using the API

You can use this project as a library as well with its useful API.

Take a look at the [SignVerify](https://github.com/mboysan/guardtime-assignment/blob/master/src/main/java/ops/SignVerify.java)
class for the available API operations.

For some examples/samples, you can also checkout the [SignVerifyTest](https://github.com/mboysan/guardtime-assignment/blob/master/src/test/java/ops/SignVerifyTest.java)
class.

You can use it in your maven projects by adding my repository and adding this project as a dependency like the following
```xml
    ...
    <repositories>
        <repository>
            <id>project-common</id>
            <name>Project Common</name>
            <url>https://github.com/mboysan/mvn-repo/raw/master</url>
        </repository>
    </repositories>
    ...
    <dependencies>
       <dependency>
           <groupId>ee.mboysan</groupId>
           <artifactId>signverify</artifactId>
           <version>1.0-SNAPSHOT</version>
       </dependency>
    </dependencies>
    ...
```

Note that, I don't promise to keep it updated in the repo.

# Future Work

Possible further improvements to make on the project:

* Discard processing file content as String objects, instead, use byte arrays to improve security.
* Support for different file charsets other than UTF-8
* Signing of non-unique file contents.
* Support for large files.
* Provide paralellization for CPU intensive Hash Tree implementation.
* Security/protection for signature files.
* A server/client based architecture? Maybe even a distributed hash processor?
