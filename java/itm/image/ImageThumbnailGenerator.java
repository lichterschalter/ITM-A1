package itm.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/*******************************************************************************
    This file is part of the ITM course 2016
    (c) University of Vienna 2009-2016
*******************************************************************************/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.FontMetrics;

import javax.imageio.ImageIO;

/**
    This class converts images of various formats to PNG thumbnails files.
    It can be called with 3 parameters, an input filename/directory, an output directory and a compression quality parameter.
    It will read the input image(s), grayscale and scale it/them and convert it/them to a PNG file(s) that is/are written to the output directory.

    If the input file or the output directory do not exist, an exception is thrown.
*/
public class ImageThumbnailGenerator 
{

    /**
        Constructor.
    */
    public ImageThumbnailGenerator()
    {
    }

    /**
        Processes an image directory in a batch process.
        @param input a reference to the input image file
        @param output a reference to the output directory
        @param rotation
        @param overwrite indicates whether existing thumbnails should be overwritten or not
        @return a list of the created files
    */
    public ArrayList<File> batchProcessImages( File input, File output, double rotation, boolean overwrite ) throws IOException
    {
        if ( ! input.exists() ) {
            throw new IOException( "Input file " + input + " was not found!" );
        }
        if ( ! output.exists() ) {
            throw new IOException( "Output directory " + output + " not found!" );
        }
        if ( ! output.isDirectory() ) {
            throw new IOException( output + " is not a directory!" );
        }

        ArrayList<File> ret = new ArrayList<File>();

        if ( input.isDirectory() ) {
            File[] files = input.listFiles();
            for ( File f : files ) {
                try {
                    File result = processImage( f, output, rotation, overwrite );
                    System.out.println( "converted " + f + " to " + result );
                    ret.add( result );
                } catch ( Exception e0 ) {
                    System.err.println( "Error converting " + input + " : " + e0.toString() );
                }
            }
        } else {
            try {
                File result = processImage( input, output, rotation, overwrite );
                System.out.println( "converted " + input + " to " + result );
                ret.add( result );
            } catch ( Exception e0 ) {
                System.err.println( "Error converting " + input + " : " + e0.toString() );
            }
        } 
        return ret;
    }  

    /**
        Processes the passed input image and stores it to the output directory.
        This function should not do anything if the outputfile already exists and if the overwrite flag is set to false.
        @param input a reference to the input image file
        @param output a reference to the output directory
        @param dimx the width of the resulting thumbnail
        @param dimy the height of the resulting thumbnail
        @param overwrite indicates whether existing thumbnails should be overwritten or not
    */
    protected File processImage( File input, File output, double rotation, boolean overwrite ) throws IOException, IllegalArgumentException
    {
        if ( ! input.exists() ) {
            throw new IOException( "Input file " + input + " was not found!" );
        }
        if ( input.isDirectory() ) {
            throw new IOException( "Input file " + input + " is a directory!" );
        }
        if ( ! output.exists() ) {
            throw new IOException( "Output directory " + output + " not found!" );
        }
        if ( ! output.isDirectory() ) {
            throw new IOException( output + " is not a directory!" );
        }

        // create outputfilename and check whether thumb already exists
        File outputFile = new File( output, input.getName() + ".thumb.png" );
        if ( outputFile.exists() ) {
            if ( ! overwrite ) {
                return outputFile;
            }
        }

        // ***************************************************************
        //  Fill in your code here!
        // ***************************************************************

        BufferedImage img;
        
        // load the input image
	    img = ImageIO.read(input);
	    
        BufferedImage imgNew;	    
	    Graphics2D grafik;
	    
	    //rotate the image if it is not in landscape-orientation
	    if( img.getHeight() > img.getWidth() ){
	    	imgNew = new BufferedImage( img.getHeight(), img.getWidth(), img.getType() );
	    	grafik = imgNew.createGraphics();
	    	AffineTransform affineRotation = AffineTransform.getRotateInstance( Math.toRadians (90), imgNew.getWidth() / 2, imgNew.getWidth() / 2 );
	    	AffineTransformOp rotationOp = new AffineTransformOp( affineRotation, AffineTransformOp.TYPE_BILINEAR );
	    	grafik.drawImage( rotationOp.filter(img, null), 0, 0, null );
	    }else{
	    	imgNew = new BufferedImage( img.getWidth(), img.getHeight(), img.getType() );
	    	grafik = imgNew.createGraphics();
	    	grafik.drawImage( img, 0, 0, null );
	    }

        // add a watermark of your choice and paste it to the image
        // e.g. text or a graphic
	    Font arial = new Font( "Arial", Font.ITALIC, 10 * imgNew.getWidth() / 100 );
	    grafik.setFont( arial );
	    grafik.setColor(Color.WHITE);
	    grafik.drawString( "Watermark", imgNew.getWidth() / 4, imgNew.getHeight() / 2 );
	
        // scale the image to a maximum of [ 200 w X 100 h ] pixels - do not distort!
        // if the image is smaller than [ 200 w X 100 h ] - print it on a [ dim X dim ] canvas!
	    double ratio = (double) imgNew.getWidth() / (double) imgNew.getHeight();
	    System.out.println( ratio );
	    grafik.drawImage(imgNew, 0, 0, 200, (int)(200 / ratio), new Color( 0, 0, 0 ), null);
	    //img = new BufferedImage( 200, (int)(200 / ratio), 1 );
	    
        // encode and save the image 
        String imgType = "";
        int slash = Math.max(input.toString().lastIndexOf('/'), input.toString().lastIndexOf('\\'));
        int point = input.toString().lastIndexOf('.');
        if ( point > slash ) {
            imgType = input.toString().substring( point + 1 );
        }else{
        	imgType = "jpeg";
        }
        outputFile = new File( output.getAbsolutePath() + "/" + input.getName() + "." + imgType );
        ImageIO.write( imgNew, imgType, outputFile );
        
        // rotate you image by the given rotation parameter
        // save as extra file - say: don't return as output file
        
        /*rotate by the given parameter the image - do not crop image parts!
        imgRot = img;
	    Graphics2D grafikRot = imgRot.createGraphics();
	    AffineTransform transform = new AffineTransform();
	    transform.rotate( Math.toRadians( 30 ) );
	    grafikRot.setTransform( transform );
	    grafikRot.drawImage(imgRot, null, 0, 0 );
	    grafikRot.dispose();*/
        
        /*save the rotated image
        File outputFileRot = new File( output.getAbsolutePath() + "/" + input.getName() + "-ROTATED-." + imgType );
        ImageIO.write( imgRot, "png", outputFileRot );*/
        

        return outputFile;

        /**
            ./ant.sh ImageThumbnailGenerator -Dinput=media/img/ -Doutput=test/ -Drotation=90
        */
    }

    /**
        Main method. Parses the commandline parameters and prints usage information if required.
    */
    public static void main( String[] args ) throws Exception
    {
        if ( args.length < 3 ) {
            System.out.println( "usage: java itm.image.ImageThumbnailGenerator <input-image> <output-directory> <rotation degree>" );
            System.out.println( "usage: java itm.image.ImageThumbnailGenerator <input-directory> <output-directory> <rotation degree>" );
            System.exit( 1 );
        }
        File fi = new File( args[0] );
        File fo = new File( args[1] );
        double rotation = Double.parseDouble( args[2] );

        ImageThumbnailGenerator itg = new ImageThumbnailGenerator();
        itg.batchProcessImages( fi, fo, rotation, true );
    }    
}