
; Field definitions
;ACTION   =      rm_Action            ; action code
;RESULT1  =      rm_Result1           ; primary return/error level
;RESULT2  =      rm_Result2           ; secondary return/result string
;ARG0     =      rm_Args              ; start of argblock
;ARG1     =      rm_Args+4            ; first argument
;ARG2     =      rm_Args+8            ; second argument

#MAXRMARG =      15                   ; maximum arguments

; Command (action) codes For message packets
#RXCOMM   =      $01000000            ; a command-level invocation
#RXFUNC   =      $02000000            ; a function call
#RXCLOSE  =      $03000000            ; close the port
#RXQUERY  =      $04000000            ; query for information
#RXADDFH  =      $07000000            ; add a function host
#RXADDLIB =      $08000000            ; add a function library
#RXREMLIB =      $09000000            ; remove a function library
#RXADDCON =      $0A000000            ; add/update a ClipList string
#RXREMCON =      $0B000000            ; remove a ClipList string
#RXTCOPN  =      $0C000000            ; open the trace console
#RXTCCLS  =      $0D000000            ; close the trace console

; Command modifier flag bits
#RXFB_NOIO   =   16                   ; suppress I/O inheritance?
#RXFB_RESULT =   17                   ; result string expected?
#RXFB_STRING =   18                   ; program is a "string file"?
#RXFB_TOKEN  =   19                   ; tokenize the command line?
#RXFB_NONRET =   20                   ; a "no-return" message?

; Modifier flags
#RXFF_RESULT =   1LSL#RXFB_RESULT
#RXFF_STRING =   1LSL#RXFB_STRING
#RXFF_TOKEN  =   1LSL#RXFB_TOKEN
#RXFF_NONRET =   1LSL#RXFB_NONRET

#RXCODEMASK  =   $FF000000
#RXARGMASK   =   $0000000F


; The RexxArg structure is identical To the NexxStr structure, but
; is allocated from system memory rather than from internal storage.
; This structure is Used For passing arguments To external programs.
; It is usually passed as an "argstring", a Pointer To the string Buffer.
        NEWTYPE.ra_Buff
           ra_Buff1.l
           ra_Buff2.l
        End NEWTYPE

         NEWTYPE.RexxArg
           ra_Size.l              ; total allocated length
           ra_Length.w            ; length of string
           ra_Flags.b             ; attribute flags
           ra_Hash.b              ; hash code
           ra_Buff                ; buffer area
         End NEWTYPE

; LABEL    RexxArg_SIZEOF       ; size: 16 bytes
; Changed to RexxArg_SIZEOF from ra_SIZEOF

; The RexxMsg structure is Used For all communications with Rexx programs.
; It is an EXEC message with a parameter Block appended.

         NEWTYPE.RexxMsg
           rm_Message.Message
           rm_TaskBlock.l         ; pointer to RexxTask structure
           rm_LibBase.l           ; library pointer
           rm_Action.l            ; command (action) code
           rm_Result1.l           ; return code
           rm_Result2.l           ; secondary result
           rm_Args0.l             ; argument block (ARG0-ARG15)
           rm_Args1.l             ; argument block (ARG0-ARG15)
           rm_Args2.l             ; argument block (ARG0-ARG15)
           rm_Args3.l             ; argument block (ARG0-ARG15)
           rm_Args4.l             ; argument block (ARG0-ARG15)
           rm_Args5.l             ; argument block (ARG0-ARG15)
           rm_Args6.l             ; argument block (ARG0-ARG15)
           rm_Args7.l             ; argument block (ARG0-ARG15)
           rm_Args8.l             ; argument block (ARG0-ARG15)
           rm_Args9.l             ; argument block (ARG0-ARG15)
           rm_Args10.l             ; argument block (ARG0-ARG15)
           rm_Args11.l             ; argument block (ARG0-ARG15)
           rm_Args12.l             ; argument block (ARG0-ARG15)
           rm_Args13.l             ; argument block (ARG0-ARG15)
           rm_Args14.l             ; argument block (ARG0-ARG15)
           rm_Args15.l             ; argument block (ARG0-ARG15)
           rm_PassPort.l          ; forwarding port
           rm_CommAddr.l          ; host address (port name)
           rm_FileExt.l           ; file extension
           rm_Stdin.l             ; input stream
           rm_Stdout.l            ; output stream
           rm_avail.l             ; future expansion
         End NEWTYPE
; LABEL    RMSIZEOF             ; size: 128 bytes
; Ranamed from rm_SIZEOF

NEWTYPE.FillStruct
   Flags.w             ;Flag block
   Args0.l             ; argument block (ARG0-ARG15)
   Args1.l             ; argument block (ARG0-ARG15)
   Args2.l             ; argument block (ARG0-ARG15)
   Args3.l             ; argument block (ARG0-ARG15)
   Args4.l             ; argument block (ARG0-ARG15)
   Args5.l             ; argument block (ARG0-ARG15)
   Args6.l             ; argument block (ARG0-ARG15)
   Args7.l             ; argument block (ARG0-ARG15)
   Args8.l             ; argument block (ARG0-ARG15)
   Args9.l             ; argument block (ARG0-ARG15)
   Args10.l             ; argument block (ARG0-ARG15)
   Args11.l             ; argument block (ARG0-ARG15)
   Args12.l             ; argument block (ARG0-ARG15)
   Args13.l             ; argument block (ARG0-ARG15)
   Args14.l             ; argument block (ARG0-ARG15)
   Args15.l             ; argument block (ARG0-ARG15)
   EndMark.l            ;End of the FillStruct
End NEWTYPE
