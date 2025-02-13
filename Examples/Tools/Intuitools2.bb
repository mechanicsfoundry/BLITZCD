;
; Intuitools v 2.01
;
; (c)1994 Acid Software
;
; For BUM subscribers only!
;

; uses two screens with same bitmap so user defined windows
; that are opened on back screen can be viewed on front

; window 0&1 used for ui 2 on for user defined (id+2)
; gadgetlists are equal to the window they appear on

; workout flags for gadgets
; sort out fonts for window
; delete gadget

; window requester
; put divides in numeric parameters eg 1/16
; generate blitz2 code

; * no code generated for shape gadgets
; * gtlist currently creates remmd line
; * constant system not supported as yet

ver$="IntuiTools v2.01"

MaxLen pa$=192:MaxLen fi$=192
MaxLen pc$=192:MaxLen fc$=192

NEWTYPE .xy:x.w:y.w:End NEWTYPE

NEWTYPE .obj
  x.w:y:w:h            ;position
  c.xy[8]              ;handles
  obj.w                ;window=0 gadget=1
  id.w
  label$:constant$
  gtype.w              ;gadgettype 0=text 1=string 2=prop 3=shape 4..=gt
  gflags.w             ;flags field compiled from below
  gstring.s            ;main string field (includes | if necesary)
  flags.w[16]
  pars$[8]
  nars.q[8]
  *win.obj             ;if not a window this points to a window
  winid.w
  ox.w:oy              ;offset from window
  c0$
End NEWTYPE

NEWTYPE .proj
  w.w:h.w:d.w:m.w    ;screen res
  font$:height.w:style.w
  numwin.w
  numgad.w
End NEWTYPE

NEWTYPE .sinfo:id.l:w.l:h.l:d.w:os.w:sc.w:bw.l:bh.l:End NEWTYPE
NEWTYPE .finfo:name.s:ysize.w:style.b:flags:p1:p2:dm:pad:End NEWTYPE

NEWTYPE .tst:a.w:c$:End NEWTYPE
Dim List test.tst(32)
While AddItem(test()):test()\c="ITEM"+Str$(i):i+1:Wend

Statement drawbox{*g.obj}
  USEPATH *g
  x=\x:y=\y
  \c[0]\x=x,y:\c[1]\x=x+\w/2-2,y:\c[2]\x=x+\w-6,y
  \c[3]\x=x,y+\h/2-1:\c[4]\x=x+\w-6,y+\h/2-1
  \c[5]\x=x,y+\h-3:\c[6]\x=x+\w/2-2,y+\h-3
  \c[7]\x=x+\w-6,y+\h-3
  USEPATH *g\c[i]
  For i=0 To 7
    Boxf \x,\y,\x+5,\y+2,-1
  Next
End Statement

Function input{}:Function Return Val(Edit$(80)):End Function

;------------------------------------------------------------------------

NEWTYPE .gad
  name$
  mask.w
  pars$[4]
End NEWTYPE

Dim gt.gad(32)

DEFTYPE .proj p
DEFTYPE .obj *w,*o     ;who wants predefined vars?

DEFTYPE .l n0,n1,n2

Dim List q.obj(50)

LoadFont 0,"topaz.font",8

q$=Chr$(34)

AddIDCMP $10                      ;get mouse move events
Gosub initgui
Gosub initwui
Gosub initmenus
p\w=640,256,2,$8000               ;default project setup
p\font="topaz.font",8,0
Gosub nuscreen

gx=1:gy=1:auto=0      ;grid setting
bx=4:by=2             ;gadget border settings
mode=0

.main
  Repeat
    WindowInput 0
    ev.l=WaitEvent
    If ev=$8 Then Gosub hitobject
    If ev=$100
      If MenuHit=0
        If ItemHit=0 Then Gosub doload
        If ItemHit=1 Then Gosub dosave
        If ItemHit=2 Then Gosub getscreen
        If ItemHit=3 Then Gosub getfont
        If ItemHit=5 Then Gosub testscreen
        If ItemHit=6 Then Gosub createcode
        If ItemHit=7 Then End
      EndIf
      If MenuHit=1
        If ItemHit=0
          If MenuChecked(0,1,0) Then gx=8:gy=4 Else gx=1:gy=1
        EndIf
        If ItemHit=1 Then auto=MenuChecked(0,1,1)
      EndIf
      If MenuHit=2
        If ItemHit=0 Then Gosub addwindow
        If ItemHit>0 Then gtype=ItemHit-1:Gosub addgadget
      EndIf
      If MenuHit=3
        gtype=ItemHit+4:Gosub addgadget
      EndIf
    EndIf
  Forever

.createcode:
  Use Screen 1:USEPATH q()
  f$=ASLFileRequest$("CREATE SOURCE CODE",pc$,fc$)
  If WriteFile(0,f$)
    FileOutput 0
    ;
    ResetList q()
    While NextItem(q()):Print "#"+\constant+"="+Str$(\id)+Chr$(0):Wend
    Print Chr$(0)
    ;
    sp$=Str$(p\w)+","+Str$(p\h)+","+Str$(p\d)+","+Str$(p\m)
    Print Mki$($cf01)+" 0,0,0,"+sp$+","+q$+"TEST"+q$+",0,1"+Chr$(0)
    ;
    ResetList q()
    While NextItem(q()):Print \c0+Chr$(0):Wend:Print Chr$(0)
    ;
    CloseFile 0
  EndIf
  Return

.testscreen:
  mode=1:Gosub nuscreen
  Repeat:ev.l=WaitEvent:Until ev=$100
  mode=0:Gosub nuscreen
  Return

.addwindow:
  MenusOff:Use Window 0:WTitle "","ADD WINDOW BY DRAGGING MOUSE"
  Repeat:ev.l=WaitEvent:Until ev=$8     ;wait for button
  x=SMouseX:y=SMouseY
  If AddLast(q())
    q()\obj=0:q()\id=p\numwin:p\numwin+1
    q()\x=x,y
    *w=q():q()\win=*w:q()\winid=*w\id
    drawbox{q()}
    i=7:Gosub sizeobject
    If q()\constant="" Then q()\constant="W"+Str$(p\numwin)
    If auto Then Gosub editobject Else Gosub redrawwindow
  EndIf
  MenusOn:Use Window 0:WTitle "",ver$:Return

.addgadget:
  If *w=0 Then BeepScreen 1:Return
  MenusOff:Use Window 0:WTitle "","ADD GADGET BY DRAGGING MOUSE"
  USEPATH q()
  Repeat:ev.l=WaitEvent:Until ev=$8     ;wait for button
  x=SMouseX:y=SMouseY
  ResetList q()
  While NextItem(q())
    If RectsHit (x,y,1,1,\x,\y,\w,\h) AND \obj=0 Then Goto gotwin
  Wend
  BeepScreen 1:MenusOn:Use Window 0:WTitle "",ver$:Return
gotwin:
  *w=\win
  If AddFirst(q())
    q()\obj=1:q()\id=p\numgad:p\numgad+1
    q()\x=x,y
    q()\win=*w:q()\winid=*w\id
    q()\gtype=gtype
    drawbox{q()}
    i=7:Gosub sizeobject
    If q()\constant="" Then q()\constant="G"+Str$(p\numgad)
    If auto Then Gosub editobject Else Gosub redrawwindow
  EndIf
  MenusOn:Use Window 0:WTitle "",ver$:Return

.hitobject:
  If MButtons=1
    x=SMouseX:y=SMouseY
    ResetList q()
    While NextItem(q())     ;check all handles
      For i=0 To 7
        If RectsHit (x,y,1,1,q()\c[i]\x,q()\c[i]\y,8,4)
          *w=q()\win:tt=0:g=0:Pop For:Goto gothit
        EndIf
      Next
    Wend
    ResetList q()
    While NextItem(q())     ;check inside complete
      If RectsHit (x,y,1,1,q()\x,q()\y,q()\w,q()\h)
        *w=q()\win:tt=0:g=0:i=-1:Goto gothit
      EndIf
    Wend
  EndIf
  Return

gothit:
  Repeat                      ;crusty double click mechanism
    VWait:tt+1:ev.l=Event
    If ev=8
      If MButtons=1 Then Goto editobject Else g=1
    EndIf
  Until tt=10 OR (ev<>8 AND ev<>0 AND ev<>$10)
  If g=0 Then Gosub sizeobject
  Gosub redrawwindow
  Return

.sizeobject:
  USEPATH q()
  Repeat
    ev.l=WaitEvent
    drawbox{q()}
    ox=x:oy=y:x=SMouseX:y=SMouseY:dx=x-ox:dy=y-oy
    Select i
      Case -1:\x+dx:\y+dy
      Case 0:\x+dx:\y+dy:\w-dx:\h-dy
      Case 1:\y+dy:\h-dy
      Case 2:\y+dy:\w+dx:\h-dy
      Case 3:\x+dx:\w-dx
      Case 4:\w+dx
      Case 5:\x+dx:\w-dx:\h+dy
      Case 6:\h+dy
      Case 7:\w+dx:\h+dy
    End Select
    \w=QLimit(\w,16,1024):\h=QLimit(\h,8,1024)
    \x=QLimit(\x,0,p\w-\w):\y=QLimit(\y,0,p\h-\h)
    drawbox{q()}
  Until ev=$8
  drawbox{q()}     ;rubout
  If gx Then \x=Int((\x+gx/2)/gx)*gx:\w=Int((\w+gx/2)/gx)*gx
  If gy Then \y=Int((\y+gy/2)/gy)*gy:\h=Int((\h+gy/2)/gy)*gy
  \x=QLimit(\x,0,p\w-\w):\y=QLimit(\y,0,p\h-\h)
  \w=QLimit(\w,16,1024):\h=QLimit(\h,8,1024)
  If \obj=1
    \ox=\x-*w\x:\oy=\y-*w\y
    If \gtype>3 Then \oy-p\height-3:\ox-4
  EndIf
  Return

.redrawwindow:
  USEPATH *w
  If *w=0 Then Return
  If q()\obj=0 Then *w=q() Else *w=q()\win
  Free Window *w\id+2:Free GadgetList *w\id+2:Free GTList *w\id+2
  PushItem q():Gosub calcgads:PopItem q()
  Use Screen mode    ;0
  ;
  \x=QLimit(\x,0,p\w-16)    ;ensure window is legal
  \y=QLimit(\y,0,p\h-4)
  \w=QLimit(\w,0,p\w-\x)
  \h=QLimit(\h,0,p\h-\y)
  ;
  Window \id+2,\x,\y,\w,\h,\gflags,\gstring,1,2,\id+2:SetMenu mode
  AttachGTList \id+2,\id+2
  If mode=0
    PushItem q()
    ResetList q():USEPATH q()
    While NextItem(q())
      If \win=*w
        \x=*w\x+\ox:\y=*w\y+\oy
        If \gtype>3 Then \y+p\height+3:\x+4
        drawbox{q()}
      EndIf
    Wend
    PopItem q()
  EndIf
  Return

Function.s totpars{*q.obj} ;create | separated par$
  USEPATH *q
  max=0:p$="":n=0
  For i=0 To 7
    l=Len(\pars[i]):If l>0 Then n=i
    If l > max Then max=Len(\pars[i])
  Next
  If n>0
    For i=0 To n:p$+"|"+Centre$(\pars[i],max):Next:p$=Mid$(p$,2)
  Else
    p$=\pars[0]
  EndIf
  Function Return p$
End Function

;toggle|pos0-3|select|slider|propbox|mx|g00|gtlabl0-3|brdr0..3|gt0..3
;  0       1      2      3       4   5   6      7      8-11    12-14

USEPATH q()
Macro p2 Str$(`1)+","+Str$(`2):End Macro
Macro p3 Str$(`1)+","+Str$(`2)+","+Str$(`3):End Macro
Macro p4 Str$(`1)+","+Str$(`2)+","+Str$(`3)+","+Str$(`4):End Macro
Macro gt ww$+gg$+!p4{\ox,\oy,\w,\h}+","+q$+\label+q$+","+Str$(\gflags):End Macro

.calcgads:
  USEPATH q()
  gt=0:bb=0:ww$=" #"+*w\constant+","
  ResetList q()
  While NextItem(q())
    If \obj=1 AND \win=*w
      gg$="#"+\constant+","
      If \gtype>3        ;gadtools!
        gt+1
        \gflags=2^\flags[7]+\flags[12]*$20+\flags[13]*$80+\flags[14]*$100
      Else
        bb+1
      EndIf
      Select \gtype
        Case 0
          \gflags=\flags[0]+\flags[1]*2+\flags[2]*32
          \gstring=totpars{q()}:If \gstring="" Then \gstring=" "
          TextGadget *w\id+2,\ox,\oy,\gflags,\id,\gstring
          \c0=Mki$($c601)+ww$+!p3{\ox,\oy,\gflags}+","+gg$+q$+\gstring+q$
        Case 1
          \gflags=\flags[1]*2+\flags[2]*32:n0=\nars[0]:If n0=0 Then n0=1
          ww=\w-bx-bx
          StringGadget *w\id+2,\ox,\oy,\gflags,\id,n0,ww
          \c0=Mki$($c602)+ww$+!p3{\ox,\oy,\gflags}+","+gg$+!p2{n0,ww}
        Case 2
          \gflags=\flags[1]*2+\flags[2]*32+(\flags[3]+1)*64+\flags[4]*256
          PropGadget *w\id+2,\ox,\oy,\gflags,\id,\w,\h
          SetVProp *w\id+2,\id,\nars[0],\nars[1]
          SetHProp *w\id+2,\id,\nars[2],\nars[3]
          \c0=Mki$($c603)+ww$+!p3{\ox,\oy,\gflags}+","+gg$+!p2{\w,\h}
          \c0+":"+Mki$($c606)+ww$+gg$+!p2{\nars[0],\nars[1]}
          \c0+":"+Mki$($c605)+ww$+gg$+!p2{\nars[2],\nars[3]}
        Case 3
          \gflags=\flags[0]+\flags[1]*2+\flags[2]*32
          If Exists(\pars[0])
            LoadShape 0,\pars[0]
            If Exists(\pars[1])
              LoadShape 1,\pars[1]
              ShapeGadget *w\id+2,\ox,\oy,\gflags,\id,0,1
            Else
              ShapeGadget *w\id+2,\ox,\oy,\gflags,\id,0
            EndIf
          EndIf
        Case 4
          GTButton *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags
          \c0=Mki$($c682)+!gt
        Case 5
          GTCheckBox *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags
          \c0=Mki$($c683)+!gt
        Case 6
          \gstring=totpars{q()}
          GTCycle *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,\gstring
          \c0=Mki$($c684)+!gt+","+q$+\gstring+q$
        Case 7
          n0=\nars[0]
          GTInteger *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,n0
          \c0=Mki$($c685)+!gt+","+Str$(n0)
        Case 8
          GTListView *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,test()
          \c0=";"+Mki$($c686)+!gt+","+\pars[0]
        Case 9
          \gstring=totpars{q()}
          GTMX *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,\gstring
          \c0=Mki$($c687)+!gt+","+q$+\gstring+q$
        Case 10
          n0=\nars[0]
          GTNumber *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,n0
          \c0=Mki$($c688)+!gt+","+Str$(n0)
        Case 11
          n0=\nars[0]
          GTPalette *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,n0
          \c0=Mki$($c689)+!gt+","+Str$(n0)
        Case 12
          n0=\nars[0]:n1=\nars[1]:n2=\nars[2]
          If \flags[3] Then \gflags+$400
          GTScroller *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,n0,n1,n2
          \c0=Mki$($c68a)+!gt+","+Str$(n0)+","+Str$(n1)+","+Str$(n2)
        Case 13
          n0=\nars[0]:n1=\nars[1]:n2=\nars[2]
          If \flags[3] Then \gflags+$400
          GTSlider *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,n0,n1,n2
          \c0=Mki$($c68b)+!gt+","+Str$(n0)+","+Str$(n1)+","+Str$(n2)
        Case 14
          n0=\nars[0]
          GTString *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,n0
          \c0=Mki$($c68c)+!gt+","+Str$(n0)
        Case 15
          p$=\pars[0]
          GTText *w\id+2,\id,\ox,\oy,\w,\h,\label,\gflags,p$ ;\pars[0]
          \c0=Mki$($c68d)+!gt+","+q$+p$+q$               ;\pars[0]
        Case 16
          If Exists(\pars[0])
            LoadShape 0,\pars[0]
            If Exists(\pars[1])
              LoadShape 1,\pars[1]
              GTShape *w\id+2,\id,\ox,\oy,\gflags,0,1
            Else
              GTShape *w\id+2,\id,\ox,\oy,\gflags,0
            EndIf
          EndIf
;          \c0=Mki$($c698)+!gt+p$      ;\pars[0]
      End Select
      If \gtype<4 Then a.l=Addr GadgetList(*w\id+2) Else a.l=Addr GTList(*w\id+2)
      While Peek.l(a)<>0
        a=Peek.l(a)
        If Peek.w(a+38)=\id
          \w=Peek.w(a+8):\h=Peek.w(a+10)
          If \gtype=1 Then \w+bx+bx:\h+by+by
          If \gtype=7 OR \gtype=14 Then \w+12:\h+6
        EndIf
      Wend
    EndIf
  Wend

  ResetList q()
  While NextItem(q())
    If \obj=0 AND \win=*w
      \c0=Mki$($c501)+" #"+*w\constant+","+!p4{\x,\y,\w,\h}
      \c0+","+Str$(\gflags)+","+q$+\gstring+q$+",1,2"
      If bb>0 Then \c0+","+Str$(\id)
      If gt>0 Then \c0+":"+Mki$($c68e)+ww$+"#"+*w\constant
    EndIf
  Wend
  Return

.doload
  Use Screen 1:USEPATH q()
  f$=ASLFileRequest$("LOAD PROJECT",pa$,fi$)
  If f$="" Then Return
  If ReadFile(0,f$)
    FileInput 0
    If Edit$(80)="IntuiProject"
      p\w=input{}:p\h=input{}:p\d=input{}:p\m=input{}
      p\font=Edit$(80):p\height=input{}:p\style=input{}
      p\numwin=input{}:p\numgad=input{}
      ClearList q()
      While NOT Eof(0)
        AddItem q()
        \x=input{}:\y=input{}:\w=input{}:\h=input{}
        \obj=input{}:\id=input{}
        \label=Edit$(80):\constant=Edit$(80)
        \gtype=input{}:\gflags=input{}:\gstring=Edit$(80)
        For i=0 To 15:\flags[i]=input{}:Next
        For i=0 To 7:\pars[i]=Edit$(80):Next
        For i=0 To 7:\nars[i]=input{}:Next
        \winid=input{}:\ox=input{}:\oy=input{}
        \c0=Edit$(128)
      Wend
      ResetList q()               ;link up window pointers
      While NextItem(q())
        If \obj=0
          *w=q():PushItem q():ResetList q()
          While NextItem(q())
            If \winid=*w\id Then \win=*w
          Wend
          PopItem q()
        EndIf
      Wend
    EndIf
    CloseFile 0
    Gosub nuscreen
  EndIf
  Return

.dosave
  Use Screen 1:USEPATH q()
  f$=ASLFileRequest$("SAVE PROJECT",pa$,fi$)
  If WriteFile(0,f$)
    FileOutput 0
    NPrint "IntuiProject"
    NPrint p\w:NPrint p\h:NPrint p\d:NPrint p\m
    NPrint p\font:NPrint p\height:NPrint p\style
    NPrint p\numwin:NPrint p\numgad
    ResetList q()
    While NextItem(q())
      NPrint \x:NPrint \y:NPrint \w:NPrint \h
      NPrint \obj:NPrint \id
      NPrint \label:NPrint \constant
      NPrint \gtype:NPrint \gflags:NPrint \gstring
      For i=0 To 15:NPrint \flags[i]:Next
      For i=0 To 7:NPrint \pars[i]:Next
      For i=0 To 7:NPrint \nars[i]:Next
      NPrint \winid:NPrint \ox:NPrint \oy
      NPrint \c0
    Wend
    CloseFile 0
  EndIf
  WindowOutput 0
  Return

.getscreen:
  Use Screen 1
  *s.sinfo=ASLScreenRequest(31)
  If *s<>0
    p\w=*s\w,*s\h,*s\d,*s\id
    Gosub nuscreen
  EndIf
  Return

.getfont:
  Use Screen 1
  *f.finfo=ASLFontRequest(15)
  If *f<>0
    p\font=*f\name,*f\ysize,*f\style
    Gosub nuscreen
  EndIf
  Return

.freeall:
  For i=1 To p\numwin
    Free Window i+1:Free GadgetList i+1:Free GTList i+1
  Next
  Return

.nuscreen:
  Gosub freeall
  CloseWindow 0:CloseScreen 0:CloseScreen 1:Free BitMap 0
  LoadFont 1,p\font,p\height,p\style:Use IntuiFont 1
  Gosub initmenus
  p\w=p\w&$fff0
  BitMap 0,p\w,p\h,p\d
  Screen 0,0,0,p\w,p\h,p\d,p\m,ver$,1,2,0
  Screen 1,0,0,p\w,p\h,p\d,p\m,ver$,1,2,0
  Use Screen 1
  Window 0,0,0,p\w,p\h,$1900,"",1,2:SetMenu mode ;0
  ResetList q()
  While NextItem(q())
    If q()\obj=0 Then Gosub redrawwindow
  Wend
  Return

.initmenus:
  MenuTitle 0,0,"PROJECT  "
    MenuItem 0,0,0,0,"LOAD               ","L"
    MenuItem 0,0,0,1,"SAVE   ","S"
    MenuItem 0,0,0,2,"SCREEN ","C"
    MenuItem 0,0,0,3,"FONT   ","F"
    MenuItem 0,0,0,4,"PALETTE","P"
    MenuItem 0,0,0,5,"TEST   ","T"
    MenuItem 0,0,0,6,"CREATE ","X"
    MenuItem 0,0,0,7,"QUIT   ","Q"
  MenuTitle 0,1,"OPTIONS  "
    MenuItem 0,1,1,0,"  GRID        "
    MenuItem 0,1,1,1,"  AUTO OPTION "
  MenuTitle 0,2,"ADD OBJECT  "
    MenuItem 0,0,2,0,"WINDOW                   ","W"
    MenuItem 0,0,2,1,"TEXT GADGET","E"
    MenuItem 0,0,2,2,"STRING GADGET","R"
    MenuItem 0,0,2,3,"PROP GADGET","O"
    MenuItem 0,0,2,4,"SHAPE GADGET","H"
  MenuTitle 0,3,"ADD GTGADGET  "
    MenuItem 0,0,3,0,"GTButton                 ","1"
    MenuItem 0,0,3,1,"GTCheckBox","2"
    MenuItem 0,0,3,2,"GTCycle","3"
    MenuItem 0,0,3,3,"GTInteger","4"
    MenuItem 0,0,3,4,"GTListView","5"
    MenuItem 0,0,3,5,"GTMX","6"
    MenuItem 0,0,3,6,"GTNumber","7"
    MenuItem 0,0,3,7,"GTPalette","8"
    MenuItem 0,0,3,8,"GTScroller","9"
    MenuItem 0,0,3,9,"GTSlider","0"
    MenuItem 0,0,3,10,"GTString","!"
    MenuItem 0,0,3,11,"GTText","@"
    MenuItem 0,0,3,12,"GTShape","@"
  MenuTitle 1,0,"TEST MENU  "
    MenuItem 1,0,0,0,"QUIT TEST                ","Q"
  Return

.editobject:
  USEPATH q()
  Use Screen 1:WindowFont 0
  wx=(p\w-320)/2:wy=(p\h-200)/2
  If \obj=0
    Window 1,wx,wy,320,200,$1000,"",1,2,1
    WindowFont 0:WColour 2
    WLocate 4,04:Print "WindowName:":SetString 1,0,\gstring:Redraw 1,0
    WLocate 4,17:Print "  Constant:":SetString 1,1,\constant:Redraw 1,1
    For i=0 To 12:Toggle 1,i+2,\gflags AND Int(2^i):Next
    Repeat
      ev.l=WaitEvent:gh=GadgetHit
      If ev=$40
        If gh=0 Then \gstring=StringText$(1,0)
        If gh=1 Then \constant=StringText$(1,1)
        If gh>1 AND gh<15
          bf.w=2^(gh-2):bm.w=65535-bf
          \gflags=(\gflags AND bm)
          If GadgetStatus(1,gh)=On Then \gflags+bf
        EndIf
        If gh=19 Then KillItem q():ResetList q():*w=0:Goto spag1
      EndIf
    Until ev=$40 AND gh=18
;    Gosub calcwin
  EndIf
  If \obj=1
    Window 1,wx,wy,320,200,$1000,"",1,2,0
    WindowFont 0:WColour 2
    WLocate 4,04:Print "GadgetType:"
    WLocate 4,17:Print " TextLabel:"
    WLocate 4,30:Print " Constant#:"
    WLocate 4,45:Print " FLAGS:"
    WLocate 4,69:Print "GIMME00"
    WLocate 4,81:Print " LABEL:"
    SetGadgetStatus 0,0,\gtype+1:Redraw 1,0
    SetString 0,1,\label:Redraw 1,1
    SetString 0,2,\constant:Redraw 1,2
    For i=0 To 7:SetGadgetStatus 0,3+i,\flags[i]+1:Redraw 1,3+i:Next
    For i=0 To 6:Toggle 0,11+i,\flags[8+i]:Redraw 1,11+i:Next
    For i=0 To 7:SetString 0,21+i,\pars[i]:Redraw 1,21+i:Next
    refreshtype:
    g=\gtype:
    For i=0 To 14
      If gt(g)\mask AND Int(2^i) Then Enable 0,i+3 Else Disable 0,i+3
      Redraw 1,i+3
    Next
    For i=0 To 3:WLocate 4,94+i*11:Print gt(g)\pars[i]:Next
    Repeat
      ev.l=WaitEvent:gh=GadgetHit
      If ev=$40
        If gh=0 Then \gtype=GadgetStatus(0,0)-1:Goto refreshtype
        If gh=1 Then \label=StringText$(0,1):ActivateString 1,2
        If gh=2 Then \constant=StringText$(0,2)
        If gh>2 AND gh<11 Then \flags[gh-3]=GadgetStatus(0,gh)-1
        If gh>10 AND gh<18 Then \flags[gh-3]=1-\flags[gh-3]
        If gh>20
          \pars[gh-21]=StringText$(0,gh)
          \nars[gh-21]=Val(\pars[gh-21])
          If gh<28 Then ActivateString 1,gh+1
        EndIf
        If gh>20
          \pars[gh-21]=StringText$(0,gh)
          \nars[gh-21]=Val(\pars[gh-21])
          If gh<28 Then ActivateString 1,gh+1
        EndIf
        If gh=19
          KillItem q():ResetList q():gh=18
          Repeat:NextItem q():Until q()=*w     ;cludge maximus
        EndIf
        If gh=20
          *o=q()
          If AddItem(q())
            q()\obj=1:q()\id=p\numgad:p\numgad+1
            q()\win=*o\win:q()\winid=*o\winid
            q()\x=*o\x,*o\y,*o\w,*o\h:q()\ox=*o\ox,*o\oy
            q()\label=*o\label
            q()\constant=*o\constant
            q()\gtype=*o\gtype,*o\gflags
            q()\gstring=*o\gstring
            For i=0 To 15:q()\flags[i]=*o\flags[i]:Next
            For i=0 To 7
              q()\pars[i]=*o\pars[i]:q()\nars[i]=*o\nars[i]
            Next
            gh=18
          EndIf
        EndIf
      EndIf
    Until ev=$40 AND gh=18
  EndIf
spag1:
  Free Window 1
  WindowInput 0:WindowOutput 0:Use Window 0:WindowFont 1
  Gosub redrawwindow
  Return

;toggle|pos0-3|select|slider|propbox|mx|g00|label0-3|brdr0..3|gt0..3

.initwui:
  Restore wads
  StringGadget 1,100,4,0,0,32,206
  StringGadget 1,100,17,0,1,32,206
  x=10:y=30
  For f=2 To 14
    Read a$:a$=Centre$(a$,17)
    TextGadget 1,x,y,1,f,a$
    x+150:If x=310 Then x=10:y+13
  Next
  TextGadget 1,20,185,0,18," OK "
  TextGadget 1,220,185,0,19," DELETE "
  Return

wads:
  Data$ WINDOWSIZING,WINDOWDRAG,WINDOWDEPTH
  Data$ WINDOWCLOSE,SIZEBRIGHT,SIZEBBOTTOM
  Data$ SIMPLEREFRESH,SUPERBITMAP,BACKDROP
  Data$ REPORTMOUSE,GIMME00,BORDERLESS
  Data$ "ACTIVATE"

.initgui:
  Restore gads:i=0:gt$=""
  Repeat
    Read a$
    If a$<>""
      gt(i)\name=a$:gt$+"|"+gt(i)\name
      For j=0 To 3:Read gt(i)\pars[j]:Next
      Read gt(i)\mask
      i+1
    EndIf
  Until a$=""
  gt$=Mid$(gt$,2)
  TextGadget 0,100,4,0,0,gt$
  StringGadget 0,100,17,0,1,32,206
  StringGadget 0,100,30,0,2,32,206
  TextGadget 0,66,44,0,3,"NO TOGGLE| TOGGLE  "
  TextGadget 0,150,44,0,4,"TOP LEFT |TOP RIGHT|BOT LEFT |BOT RIGHT"
  TextGadget 0,234,44,0,5,"INVSELECT|BOXSELECT"
  TextGadget 0,66,56,0,6,"HORIZONTL|VERTICAL |HOR&VERT "
  TextGadget 0,150,56,0,7,"PROP BOX |NOPROPBOX"
  TextGadget 0,234,56,0,8," NOT MX  | MX TYPE "
  TextGadget 0,66,68,0,9,"OUTSIDEBORDER|INSIDEBORDER"
  TextGadget 0,66,80,0,10," LEFT_LABEL |RIGHT_LABEL|  TOP_LABEL|BOTTM_LABEL|INNER_LABEL"
  TextGadget 0,183,68,1,11," L "
  TextGadget 0,216,68,1,12," R "
  TextGadget 0,249,68,1,13," T "
  TextGadget 0,282,68,1,14," B "
  TextGadget 0,170,80,1,15," HI "
  TextGadget 0,210,80,1,16," IM "
  TextGadget 0,250,80,1,17," ON "
  TextGadget 0,20,185,0,18," OK "
  TextGadget 0,100,185,0,19," DELETE "
  TextGadget 0,200,185,0,20," DUPLICATE "
  For i=0 To 7:StringGadget 0,80,94+i*11,0,i+21,32,226:Next
  Return

gads:
  Data.s "   TextGadget   ","Option 1:","Option 2:","Option 3:"," ``   '' "
  Data.w -1
  Data.s "  StringGadget  ","MaxChars=","         ","         ","         "
  Data.w -1
  Data.s "   PropGadget   ","VPropPot=","VPropBod=","HPropPot=","HPropBod="
  Data.w -1
  Data.s "  Shape Gadget  ","FileName1","FileName2","         ","         "
  Data.w -1
  Data.s "    GTButton    ","         ","         ","         ","         "
  Data.w -1
  Data.s "   GTCheckBox   ","         ","         ","         ","         "
  Data.w -1
  Data.s "     GTCycle    ","Option 1:","Option 2:","Option 3:"," ``   '' "
  Data.w -1
  Data.s "    GTInteger   "," Default=","         ","         ","         "
  Data.w -1
  Data.s "   GTListView   ","ListName:","         ","         ","         "
  Data.w -1
  Data.s "      GTMX      ","Option 1:","Option 2:","Option 3:"," ``   '' "
  Data.w -1
  Data.s "    GTNumber    ","   Value=","         ","         ","         "
  Data.w -1
  Data.s "    GTPalette   ","   Depth=","         ","         ","         "
  Data.w -1
  Data.s "   GTScroller   "," Visible:","   Total:","   Value:","         "
  Data.w -1
  Data.s "    GTSlider    ","     Min:","     Max:","   Value:","         "
  Data.w -1
  Data.s "    GTString    ","  MaxLen=","         ","         ","         "
  Data.w -1
  Data.s "     GTText     ","   Value=","         ","         ","         "
  Data.w -1
  Data.s "     GTShape    ","FileName1","FileName2","         ","         "
  Data.w -1
  Data.s ""
