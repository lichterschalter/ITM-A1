#!/bin/bash

# ---------------------------------------------------------------------------------------------------------
#	Backup last results
# ---------------------------------------------------------------------------------------------------------

# cp Test.txt Test.txt.bak

# ---------------------------------------------------------------------------------------------------------
#	Delete last results
# ---------------------------------------------------------------------------------------------------------

# rm -rf Test.txt

# ---------------------------------------------------------------------------------------------------------
#	Set input params
# ---------------------------------------------------------------------------------------------------------

input="$1"
output="$2"

# ---------------------------------------------------------------------------------------------------------
#	ANT
# ---------------------------------------------------------------------------------------------------------

function ant {
    # You need local admin rights to do this
    chmod +x ./ant.sh
    chmod +x tools/ant/apache-ant-1.7.0/bin/ant

    ./ant.sh clean
    ./ant.sh compile
    ./ant.sh ImageConverter 		    -Dinput=$input	-Doutput=$output 	-Dformat=bmp
    ./ant.sh ImageConverter 		    -Dinput=$input 	-Doutput=$output 	-Dformat=png
    ./ant.sh ImageConverter 		    -Dinput=$input	-Doutput=$output 	-Dformat=jpeg 	-Dquality=0.1
    ./ant.sh ImageConverter 		    -Dinput=$input	-Doutput=$output 	-Dformat=jpeg 	-Dquality=0.3
    ./ant.sh ImageConverter 		    -Dinput=$input  -Doutput=$output 	-Dformat=jpeg 	-Dquality=0.9
    ./ant.sh ImageThumbnailGenerator    -Dinput=$input 	-Doutput=$output 	-Drotation=90
    ./ant.sh ImageMetadataGenerator  	-Dinput=$input 	-Doutput=$output 
    ./ant.sh ImageHistogramGenerator 	-Dinput=$input 	-Doutput=$output 	-Dbins=256
    
    end
}

# ---------------------------------------------------------------------------------------------------------
#	Try to create output folder
# ---------------------------------------------------------------------------------------------------------

function createoutputfolder {
    if [ -z "$output" ]; 
        then output="out"
    fi
    mkdir -p $output
}

# ---------------------------------------------------------------------------------------------------------
#	ERR
# ---------------------------------------------------------------------------------------------------------

# filenotfound
function directorynotfound {
    echo "Input Folder does not exists"
    exit
}

# wrongparam
function wrongparam {
    echo "One or more parameter are missing"
    echo "Example usage: cmd media\img output"
    exit
}

# ---------------------------------------------------------------------------------------------------------
#	END
# ---------------------------------------------------------------------------------------------------------

# end
function end {
    exit
}

# ---------------------------------------------------------------------------------------------------------
#	Check input
# ---------------------------------------------------------------------------------------------------------

if [ -z "$input" ]; 
    then wrongparam
fi

if [ -z "$output" ]; 
    then createoutputfolder
fi

if [ ! -d $input ]; 
    then directorynotfound
fi

if [ ! -d $output ]; 
    then createoutputfolder
fi

if [ ! -d $input ] && [ ! -d $output ];
    then end
else
    ant
fi