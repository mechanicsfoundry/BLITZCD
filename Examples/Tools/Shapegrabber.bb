NoCli

Dim sd(150)
en=0
NW=1:ct=0:gx=8:gy=8:xo=0:yo=0 :xp=-1:yp=-1:shp=0:shh=0:shw=0
LoadShapes 159,190,"data/gadsnap.shp"
LoadShapes 158,158,"data/about.shp"

ShapeGadget 0,2,20,0,1,160,161
ShapeGadget 0,2,31,0,2,162,163
ShapeGadget 0,2,42,0,3,164,165
ShapeGadget 0,2,53,0,4,166,167
ShapeGadget 0,2,64,0,5,168,169
ShapeGadget 0,2,75,0,6,170,171
ShapeGadget 0,2,86,0,7,172,173
ShapeGadget 0,2,97,0,8,174,175
ShapeGadget 0,2,119,0,10,178,179
ShapeGadget 0,2,130,0,11,180,181
ShapeGadget 0,2,141,0,12,182,183
ShapeGadget 0,2,152,0,13,184,185
ShapeGadget 0,2,163,0,14,186,187
ShapeGadget 0,2,108,0,15,188,189

Screen 0,0,0,640,200,4,$8000," Shape Graber By King Fuzzy",1,6
RGB 0,0,0,0
RGB 1,13,13,13
RGB 2,2,3,3
RGB 3,4,4,5
RGB 4,1,4,7
RGB 5,5,5,7
RGB 6,6,7,7
RGB 7,6,7,9
RGB 8,0,5,12
RGB 9,3,6,11
RGB 10,4,8,12
RGB 11,6,9,12
RGB 12,9,9,10
RGB 13,9,10,12
RGB 14,10,11,13
RGB 15,1,2,3


DefaultIDCMP $118
Window 0,0,0,640,200,$1A00,"",2,5
Wi=640:Hi=200:dp=4
LoadScreen 0,"data/Blitz2.logo2",0

MaxLen tpa$=160
MaxLen tfl$=160

MenuTitle 0,0," Project "
MenuItem  0,0,0,0,"Load Iff        ","L"
MenuItem  0,0,0,1,"Merge Shapes    ","M"
MenuItem  0,0,0,2,"Save Shapes     ","S"
MenuItem  0,0,0,3,"About           ","A"
MenuItem  0,0,0,4,"Edit Shapes     ","E"
MenuItem  0,0,0,5,"Quit            ","Q"

MenuTitle 0,1," Options "
MenuItem  0,0,1,0,"Set Grid Size"
MenuItem  0,1,1,1,"  Grid ON/OFF           ","G"
MenuItem  0,3,1,2,"  Copy Left border      ","4"
MenuItem  0,3,1,3,"  Copy Right border     ","6"
MenuItem  0,3,1,4,"  Copy Top border       ","8"
MenuItem  0,3,1,5,"  Copy Bottom border    ","2"
MenuItem  0,0,1,6,"  Palette               ","P"




.lod:
fl$=FileRequest$("Select an IFF file",tpa$,tfl$)
If fl$="" AND NW=1 Then Goto skp1
If fl$="" Then Goto lop

ILBMInfo fl$
Hi=ILBMHeight:Wi=ILBMWidth:dp=ILBMDepth
Free Window 0
Free Screen 0
Screen 0,0,0,Wi,Hi,dp,ILBMViewMode," ",1,2
DefaultIDCMP $118
Window 0,0,0,Wi,Hi,$1A00,"",1,2

LoadScreen 0,fl$,0
Use Palette 0

skp1:
SetMenu 0
skp2:
Use Screen 0
Use Window 0
ScreensBitMap 0,0
WJam 2
NW=0
Gosub crhrf
.lop:
ev=WaitEvent
If ev=$100 Then Goto mnu
If ev=$10 Then Goto mmv
If ev=$8 Then Goto snap
Goto lop

mmv:
Gosub DMP
If xp=lx AND yp=ly Then Goto lop
Gosub crhr
Goto lop
crhr:
Wline lx,0 , lx,Hi,3
Wline 0,ly , Wi,ly,3
crhrf:
Wline xp,0 , xp,Hi,3
Wline 0,yp , Wi,yp,3
Return

.DMP:
lx=xp:ly=yp
xp=EMouseX:yp=EMouseY:bt=MButtons
FlushEvents $10
If NOT(MenuChecked (0,1,1)) Then Goto nog
tx=Int((xp-xo)/gx):ty=Int((yp-yo)/gy)
tx=tx*gx+xo:ty=ty*gy+yo
xp=xp-tx:yp=yp-ty
If xp>(gx/2) Then tx=tx+gx
If yp>(gy/2) Then ty=ty+gy
If xp>gx Then tx=tx+gx
If yp>gy Then ty=ty+gy
xp=tx:yp=ty
nog:
Return


.mnu:
m1=MenuHit
m2=ItemHit
If m1=0 AND m2=0 Then Goto lod
If m1=0 AND m2=1 Then Goto LdShapes
If m1=0 AND m2=2 Then Goto sav
If m1=0 AND m2=3 Then Goto About
If m1=0 AND m2=4 Then Goto Eddit
If m1=0 AND m2=5 Then Goto kill
If m1=1 AND m2=0 Then Goto gdsz
If m1=1 AND m2=6 Then Goto pal
If m1=1 AND m2>0 AND m2<6 Then Goto lop

WLocate 20,20
Print m1,"  ",m2,"    "
Goto lop
.gdsz:
Gosub crhrf
Wnd.l=Peek.l(Addr Window(0))
t.l=ModifyIDCMP_(Wnd,$18)
pmouse:
FlushEvents $10
Gosub drgd
e=WaitEvent
Gosub drgd
If e=$10 Then Goto pmse
If e=8 Then Goto mbtt
Goto mbt
pmse:
xo=EMouseX-gx*4:yo=EMouseY-gy*4
Goto pmouse

mbtt:
If MButtons <>1 Then Goto mbr
mbt:
FlushEvents $10
Gosub drgd
e=WaitEvent
Gosub drgd
If e=$10 Then Goto pmuse
If e=8 Then Goto mbr
Goto mbt
pmuse:
gx=Int((EMouseX-xo)/4):gy=Int((EMouseY-yo)/4)
If gx<1 Then gx=1
If gy<1 Then gy=1
Goto mbt
mbr:
FlushEvents 8
If (xo-gx)>(gx/2) Then xo=xo-gx:Goto mbr
If (yo-gy)>(gy/2) Then yo=yo-gy:Goto mbr

t.l=ModifyIDCMP_(Wnd,$118)
Gosub crhrf
Goto lop

drgd:
WJam 2
For a=0 To 4
Wline xo+gx*a,yo , xo+gx*a,yo+gy*4,3
Next
For a=0 To 4
Wline xo,yo+gy*a , xo+gx*4,yo+gy*a,3
Next


Return
.kill:
Free Window 0
Free Screen 0
End
.sav:
If shp=0 Then Goto lop
fl$=FileRequest$("Select file for Shapes",tpa$,tfl$)
If fl$="" Then Goto lop
SaveShapes 0,shp-1,fl$
Goto lop
.snap:
If MButtons <> 1 Then FlushEvents 8: Goto lop
Gosub crhrf
Gosub DMP
x1=xp:y1=yp
x2=x1:y2=y1
snp:
Gosub bx
e=WaitEvent
Gosub bx
If e=$100 Goto snp
If e=$10 Then Goto mvb
If e=8 Then Goto fsp
Goto snp
mvb:
Gosub DMP
x2=xp:y2=yp
Goto snp
bx:
Wline x1,y1,x1,y2,3
Wline x1,y2,x2,y2,3
Wline x1,y1,x2,y1,3
Wline x2,y1,x2,y2,3

Return
fsp:
If x1>x2 Then Exchange x1,x2
If y1>y2 Then Exchange y1,y2
If  NOT MenuChecked (0,1,2) Then x1+1
If  NOT MenuChecked (0,1,4) Then y1+1
If  NOT MenuChecked (0,1,3) Then x2-1
If  NOT MenuChecked (0,1,5) Then y2-1
w=x2-x1+1:h=y2-y1+1
If w<1 OR h<1 Then Goto nos
GetaShape shp,x1,y1,w,h
sd(shp)=dp
shp+1
If w>shw Then shw=w
If h>shh Then shh=h

nos:
FlushEvents
Gosub crhrf
Goto lop
.Eddit:
Gosub crhrf
ednx:
rn=0
reed:
cs=0
If shp=0 Then Goto skp2
t$=" Shape Editor : Shape # "+Str$(en+1)+" of "+Str$(shp)
Screen 1,0,0,640,400,sd(en),0,t$,2,3
CM.l=Peek.l(ViewPort(0)+4)
a=LoadRGB4_(ViewPort(1),Peek.l(CM+4),32)


DefaultIDCMP $40
Window 1,0,0,640,200,$1800,"",2,3,0
ScreensBitMap 1,0
WLocate 30,1
WColour 2,0
NPrint t$

w=ShapeWidth(en):h=ShapeHeight(en):ns=0:x=0:y=0
If w>620 OR h>370 Then ns=1

If rn=0 Then CopyShape en,155
bord:
If ns=0 Then Blit en,100,20
If ns=1 Then Blit 159,100,20

WLocate 30,9
WJam 1
WColour 2,0
NPrint "Width ",w,"  Height ",h,"         "
WJam 0
Wline 99+x,19+y ,100+x+w,19+y,3
Wline 99+x,19+y ,99+x,20+y+h,3
Wline 100+x+w,19+y ,100+x+w,20+y+h,3
Wline 99+x,20+y+h ,100+x+w,20+y+h,3

fl:
FlushEvents
e=WaitEvent
If e <> $40 Then Goto fl
On GadgetHit Goto nxs,pvs,swp,mov,rst,flx,fly,rot,rsz,trt,trl,trr,trb,qut,del
Goto fl
rsz:

qut:
If ns=0 Then Gosub fix
Free Window 1
Free Screen 1
Goto skp2
nxs:
If ns=0 Then Gosub fix
en+1
If en => shp Then en=0
Free Window 1
Free Screen 1
Goto ednx
pvs:
If ns=0 Then Gosub fix
en-1
If en < 0  Then en=shp-1
Free Window 1
Free Screen 1
Goto ednx
trt:
If h>1 Then y+1:h-1:cs=1
Goto bord
trl:
If w>1 Then x+1:w-1:cs=1
Goto bord
trb:
If h>1 Then h-1:cs=1
Goto bord
trr:
If w>1 Then w-1:cs=1
Goto bord
fix:
If cs=0 Then Return
Cls 0
Blit en,0,0
GetaShape en,x,y,w,h
Return
flx:
XFlip en
Goto bord
fly:
YFlip en
Goto bord
rot:
rn=rn+.125:If rn>= 1 Then rn=0
CopyShape 155,en
Rotate en,rn
Free Window 1
Free Screen 1
Goto reed

rst:
CopyShape 155,en
Free Window 1
Free Screen 1
Goto ednx
del:
If shp=1 Then shp=0:Goto qut
shp-1
If en=shp Then en-1:Goto dodsp
For a=en To shp-1
CopyShape a+1,a
sd(a)=sd(a+1)
Next
dodsp:
Free Shape shp
Free Window 1
Free Screen 1
Goto ednx

.snum:
DefaultIDCMP $400
Window 2,100,30,120,70,$1000,tt$,1,2
WJam1
WColour 1,0
WLocate 20,9
NPrint "Select"
WLocate 40,19
NPrint "A"
WLocate 20,29
NPrint "Shape"
WLocate 30,39
WindowInput 2
EditFrom 2
op=Edit (en+1,2)
Free Window 2
op-1
Return
swp:
tt$="  SWAP  "
Gosub snum
If op>=shp Then Goto dodsp
CopyShape op,en
CopyShape 155,op
Exchange sd(en),sd(op)
Goto dodsp
mov:
tt$="  MOVE  "
Gosub snum
If op>=shp Then Goto dodsp
s=Sgn(op-en)
If s=0 Then Goto dodsp
td=sd(en)
For a=en+s To op Step s
CopyShape a,a-s
sd(a-s)=sd(a)
Next
CopyShape 155,op
sd(op)=td
Goto dodsp
.About:
Gosub crhrf
Screen 1,0,0,320,200,2,0,"",1,2
RGB 0,0,0,0
RGB 2,15,0,0
RGB 3,15,8,0
RGB 1,15,15,15

DefaultIDCMP $200
Window 1,0,0,320,200,$1008,"",1,0
ScreensBitMap 1,0

Blit 158,60,40

FlushEvents
e=WaitEvent
Free Window 1
Free Screen 1
Goto skp2

.pal:
rq.l=OpenLibrary_ ("req.library",2)
If rq=0 Then Goto lop
Poke.l ?Req+4,Peek.l(Addr Window(0))
GetReg d0,rq
MOVEM.l  d1-d7/a0-a6,-(a7)
MOVE.l   d0,a6
MOVEQ.l  #0,d0
LEA      Req,a0
JSR      -$C0(a6)
MOVEM.l  (a7)+,d1-d7/a0-a6
a=CloseLibrary_(rq)

Goto lop
Req:     Dc.l     0,0,0,0,0,0,0,0,0,0

.LdShapes:
fl$=FileRequest$("Load a Shape file",tpa$,tfl$)
If fl$="" Then Goto lop
e= Exists(fl$)
If e=0 Then Goto lop
LoadShapes shp,150,fl$
shp=0
For a=0 To 150
ad.l= Addr Shape (a)
If Peek.w(ad)=0 Then Goto nxls
If a=shp Then Goto shok
CopyShape a,shp
Free Shape a
shok:
ad.l= Addr Shape (shp)
sd(shp)=Peek.w(ad+4)
shp+1
nxls:
Next
Goto lop


