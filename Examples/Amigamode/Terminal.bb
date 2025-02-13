;
; tiny terminal program using serial port by Simon
;

MenuTitle 0,0,"Projects"
MenuItem 0,0,0,0,"Quit    ","Q"

Screen 0,12,"BlitzTerm"

winheight=DispHeight-12
columns=Int(winheight/8)-2

Window 0,0,12,640,winheight,$1900,"",0,2,1
SetMenu 0

WindowOutput 0:WindowInput 0

If OpenSerial("serial.device",0,2400,0)=0 Then End

Repeat
  ev.l=Event:If ev=256 Then End
  WLocate x*8,y*8:WBox x*8,y*8,x*8+8,y*8+7,3  ;cursor on
  a$=Inkey$
  If a$<>"" Then WriteSerial 0,Asc(a$)
  b.w=ReadSerial(0)
  If b>0
    Select b
      WBox x*8,y*8,x*8+8,y*8+7,0              ;cursor off
      Case 10
      Case 8
        x-1
      Case 13
        If y<columns
          NPrint "":y+1:x=0
        Else
          WScroll 0,0,640,winheight,0,8:x=0
        EndIf
      Default:Print Chr$(b):x+1
    End Select
  EndIf
Forever

