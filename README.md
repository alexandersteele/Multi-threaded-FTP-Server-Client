# Multi-threaded-FTP-Server-Client
A file transfer client and server capable of transmitting files via data streams from client to server and vice versa.  The server also
logs requests with the date of the request.
Improvements required: Dynamic allocation of file sizes, improved client-side validation

Commands:
* list: list all files in the serverFiles directory
* get <filename>: retrieve a file from serverFiles (placing it in clientFiles)
* put <filename>: put a file from clientFiles in serverFiles
* bye: terminate client connection
