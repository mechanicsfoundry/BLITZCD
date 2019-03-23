;
; takes text file and prints address labels
;
; fields should be on their own line
;
; records must be separated by a blank line
;
; outputs filename.prt which you can copy straight to printer
;
; sticker format is 2 across by 9 lines down
;

NEWTYPE .user
  t$[10]
End NEWTYPE

Dim List u.user(5000)

f$="filename"           ;put filename here!

If ReadFile(0,f$)       ;read in records
  FileInput 0
  i=-1
  While NOT Eof(0)
    a$=Edit$(80)
    If a$=""
      i=-1              ;blank line flag new record
    Else
      i+1               ;else fill in details
      If i=0 Then AddItem u()
      u()\t[i]=a$
    EndIf
  Wend
  CloseFile 0
Else
  End
EndIf

If WriteFile(0,f$+".prt") ;create .prt file (could be just prt:)
  FileOutput 0
  ResetList u()
  While NextItem(u())     ;get pointers to a pair of records
    *a.user=u()
    If NextItem(u()) Then *b.user=u()
    For i=0 To 8
      a$=*a\t[i]+String$(Chr$(32),42-Len(*a\t[i]))+*b\t[i]
      NPrint a$
    Next
  Wend
  CloseFile 0
Else
  NPrint "Can't open output .prt file"
  MouseWait
EndIf
