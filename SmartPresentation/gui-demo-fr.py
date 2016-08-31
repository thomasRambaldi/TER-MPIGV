#!/usr/bin/env python
#
# Copyright (c) 2013 Tanel Alumae
# Copyright (c) 2008 Carnegie Mellon University.
#
# Inspired by the CMU Sphinx's Pocketsphinx Gstreamer plugin demo (which has BSD license)
#
# Licence: BSD

# Voici la demo kaldi qui permet de parler en direct à votre machine et sur laquelle nous avons rajouté un client OSC 

import sys
import os
import gi
import OSC
import threading
import socket

gi.require_version('Gst', '1.0')
from gi.repository import GObject, Gst, Gtk, Gdk

"""#------OSC Server-------------------------------------#
receive_address = '127.0.0.1', 9001
# OSC Server. there are three different types of server. 
s = OSC.ThreadingOSCServer(receive_address)"""

client = OSC.OSCClient()
msg = OSC.OSCMessage()
msg.setAddress("/test")

#s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)


GObject.threads_init()
Gdk.threads_init()

Gst.init(None)

class DemoApp(object):

    """GStreamer/Kaldi Demo Application"""
    def __init__(self):
        
        """Initialize a DemoApp object"""
        self.init_gui()
        self.init_gst()

    def init_gui(self):
        """Initialize the GUI components"""
        self.window = Gtk.Window()
        self.window.connect("destroy", self.quit)
        self.window.set_default_size(400,200)
        self.window.set_border_width(10)
        vbox = Gtk.VBox()        
        self.text = Gtk.TextView()
        self.textbuf = self.text.get_buffer()
        self.text.set_wrap_mode(Gtk.WrapMode.WORD)
        vbox.pack_start(self.text, True, True, 1)
        self.button = Gtk.Button("Speak")
        self.button.connect('clicked', self.button_clicked)
        vbox.pack_start(self.button, False, False, 5)
        self.window.add(vbox)
        self.window.show_all()

    def quit(self, window):
        """s.close()"""
        Gtk.main_quit()
    
    def init_gst(self):
        """Initialize the speech components"""
        self.pulsesrc = Gst.ElementFactory.make("pulsesrc", "pulsesrc")
        if self.pulsesrc == None:
            print >> sys.stderr, "Error loading pulsesrc GST plugin. You probably need the gstreamer1.0-pulseaudio package"
            sys.exit()	
        self.audioconvert = Gst.ElementFactory.make("audioconvert", "audioconvert")
        self.audioresample = Gst.ElementFactory.make("audioresample", "audioresample")    
        self.asr = Gst.ElementFactory.make("kaldinnet2onlinedecoder", "asr")
        self.fakesink = Gst.ElementFactory.make("fakesink", "fakesink")
        
        repertory = sys.argv[1]
        
        if self.asr:
          self.asr.set_property("fst", "models/%s/HCLG.fst" % repertory)
          self.asr.set_property("model", "base-fr/final.mdl")
          self.asr.set_property("word-syms", "models/%s/words.txt" % repertory)
          self.asr.set_property("feature-type", "mfcc")
          self.asr.set_property("mfcc-config", "base-fr/conf/mfcc_hires.conf")
          self.asr.set_property("ivector-extraction-config", "base-fr/conf/ivector_extractor.fixed.conf")
          self.asr.set_property("max-active", 7000)
          self.asr.set_property("beam", 6.0)
          self.asr.set_property("lattice-beam", 6.0)
          self.asr.set_property("acoustic-scale", 0.1)
          self.asr.set_property("min-words-for-ivector", 1)
          self.asr.set_property("do-endpointing", True)
          self.asr.set_property("endpoint-silence-phones", "1:2:3:4:5:6:7:8:9:10:11:12:13:14:15:16:17:18:19:20:21:22:23:24:25")
          self.asr.set_property("use-threaded-decoder", False)
          self.asr.set_property("chunk-length-in-secs", 0.2)
        else:
          print >> sys.stderr, "Couldn't create the kaldinnet2onlinedecoder element. "
          if os.environ.has_key("GST_PLUGIN_PATH"):
            print >> sys.stderr, "Have you compiled the Kaldi GStreamer plugin?"
          else:
            print >> sys.stderr, "You probably need to set the GST_PLUGIN_PATH envoronment variable"
            print >> sys.stderr, "Try running: GST_PLUGIN_PATH=../src %s" % sys.argv[0]
          sys.exit();
        
        # initially silence the decoder
        self.asr.set_property("silent", True)
        
        self.pipeline = Gst.Pipeline()
        for element in [self.pulsesrc, self.audioconvert, self.audioresample, self.asr, self.fakesink]:
            self.pipeline.add(element)         
        self.pulsesrc.link(self.audioconvert)
        self.audioconvert.link(self.audioresample)
        self.audioresample.link(self.asr)
        self.asr.link(self.fakesink)    
  
        self.asr.connect('partial-result', self._on_partial_result)
        self.asr.connect('final-result', self._on_final_result)        
        self.pipeline.set_state(Gst.State.PLAYING)
        #self.launch_server()


    def _on_partial_result(self, asr, hyp):
        """Delete any previous selection, insert text and select it."""
        Gdk.threads_enter()
        # All this stuff appears as one single action
        self.textbuf.begin_user_action()
        self.textbuf.delete_selection(True, self.text.get_editable())
        self.textbuf.insert_at_cursor(hyp)
        ins = self.textbuf.get_insert()
        iter = self.textbuf.get_iter_at_mark(ins)
        iter.backward_chars(len(hyp))
        self.textbuf.move_mark(ins, iter)
        #~ print(hyp)
        
        self.textbuf.end_user_action()    
        Gdk.threads_leave()
                
    def _on_final_result(self, asr, hyp):
        Gdk.threads_enter()
        """Insert the final result."""
        # All this stuff appears as one single action
        self.textbuf.begin_user_action()
        self.textbuf.delete_selection(True, self.text.get_editable())
        self.textbuf.insert_at_cursor(hyp)
        if (len(hyp) > 0):
            self.textbuf.insert_at_cursor(" ")
            self.launch_client(hyp)
        self.textbuf.end_user_action()
        Gdk.threads_leave()


    def button_clicked(self, button):
        """Handle button presses."""
        if button.get_label() == "Speak":
            button.set_label("Stop")
            self.asr.set_property("silent", False)
        else:
            button.set_label("Speak")
            self.asr.set_property("silent", True)

    """SERVEUR RECEVEUR"""    
    def printing_handler(self, addr, tags, stuff, source):
        if addr=='/test':
            print "Test", stuff 

    def startServer(self):
        # Start OSCServer
        print "Starting OSCServer"
        st = threading.Thread(target=s.serve_forever)
        st.start()
        

    def launch_server(self):
        # this registers a 'default' handler (for unmatched messages)
        s.addDefaultHandlers()
        # define a message-handler function for the server to call.
        s.addMsgHandler("/test", self.printing_handler)
        self.startServer()
    """FIN SERVEUR RECEVEUR"""
    

   """ CODE DU CLIENT """
    def launch_client(self, hyp):
        msg.append(hyp)
        try:
	    client.sendto(msg, ('127.0.0.1', 9001))
            msg.clearData()		
        except:
            print 'Connection refused'
            
            

if __name__ == '__main__':
  app = DemoApp()
  Gdk.threads_enter()
  Gtk.main()
  Gdk.threads_leave()

