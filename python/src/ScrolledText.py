from tkinter import *
class ScrolledText(Frame):
    def __init__(self, parent=None, text='', file=None):
        Frame.__init__(self, parent)
        self.pack(expand=YES, fill=BOTH) # make me expandable
        self.makewidgets()
    def makewidgets(self):
        sbar = Scrollbar(self)
        text = Text(self, relief=SUNKEN)
        sbar.config(command=text.yview) # xlink sbar and text
        text.config(yscrollcommand=sbar.set) # move one moves other
        sbar.pack(side=RIGHT, fill=Y) # pack first=clip last
        text.pack(side=LEFT, expand=YES, fill=BOTH) # text clipped first+
        labelfont = ('times', 12, 'normal') # family, size, style
        text.config(font=labelfont)
        text.config(width=50)
        text.config(height=20)
        self.text = text
    def settext(self,text='', file=None,clear=True):
        origin = self.gettext()
        self.text.config(state='normal')
        self.text.delete('1.0', END) # delete current text
        if clear:
            self.text.insert('1.0', text) # add at line 1, col 0
        else:
            self.text.insert('1.0', origin + text) # add at line 1, col 0
        self.text.mark_set(INSERT, '1.0') # set insert cursor
        self.text.focus() # save user a click
        self.text.see(END)
        self.text.config(state='disabled')
    def gettext(self): # returns a string
        return self.text.get('1.0', END+'-1c') # first through last
if __name__ == '__main__':
    root = Tk()
    if len(sys.argv) > 1:
        st = ScrolledText(file=sys.argv[1]) # filename on cmdline
    else:
        st = ScrolledText(text='yyy\nmmm\n你好') # or not: two lines
    def show(event):
        print(repr(st.gettext())) # show as raw string
    root.bind('<Key-Escape>', show) # esc = dump text
    root.mainloop()