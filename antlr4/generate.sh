#!/bin/sh

# https://get-coursier.io/docs/cli-installation
# cs bootstrap org.antlr:antlr4:4.10.1 -o ~/bin/antlr4 -M org.antlr.v4.Tool

antlr4 -o .antlr ISL.g4 
