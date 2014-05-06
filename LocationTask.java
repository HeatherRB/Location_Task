// HEATHER REEVE-BLACK
// Feb 2014
//
// Java libraries
import java.io.*; // Provides for system output through data streams (here: BufferedWriter)
//
//
///////////////////////////////////////////
//
// READ ME
//
///////////////////////////////////////////
//
// This program reads an array of test locations (test_database.csv), given in latitude-longitude coordinates,
// each of which is annotated with a postcode. It then reads an array of query locations (test_queries.csv),
// and associates each query location with the postcode of the nearest test location.
// The output is written to file as a list (test_queries_matched.csv), and also in maple format (maple_data),
// as a list of location-postcode vectors.
//
// To compile and run this program, simply load the 'runMe' file at the command prompt:
// > sh runMe
//
//
//
// Main class
public class LocationTask {

	static double[][] testData = new double[5000][2];
	static double[][] queryData = new double[100][2];
	static String[] pcds = new String[5000];

	public static void main(String[] args) {

		ReadFileTest(); // reads the data from test_database.csv
		ReadFileQueries(); // reads the data from test_queries.csv
		MatchPcds(); // matches each query with the closest test postcode

	}

///////////////////////////////////////////
//
// MATCH POSTCODES
//
///////////////////////////////////////////

	public static void MatchPcds() {

		String fileOut = new String("test_queries_matched.csv");
		int match=0; // the index of the closest postcode in testData
		double dist=0.0d,match_dist=0.0d; // proportional to the distance from the closest postcode in testData

		try { // open file reader/writer
 
		BufferedWriter out = new BufferedWriter(new FileWriter(fileOut,false));
		BufferedWriter out2 = new BufferedWriter(new FileWriter("maple_data",true));
 
		// write header
		out.write("\"lat\",\"lon\",\"pcds\"");
		out.newLine();
		out2.write("Matches:=[");

		///////////////////////////////////////////////////////////////////
		// go through the array queryData, finding the closest point in testData
		///////////////////////////////////////////////////////////////////

		for (int n=0; n<100; n++) { // n refers to the query

			match=0;
			match_dist=Angle(queryData[n][0],queryData[n][1],testData[0][0],testData[0][1]);

			for (int m=1; m<5000; m++) { // m refers to the test data
				// check if distance to mth test data is less than the current closest match
				dist = Angle(queryData[n][0],queryData[n][1],testData[m][0],testData[m][1]);
				if( 1.0d-dist < 1.0d-match_dist ) {
					// if so, update the current best match
					match = m;
					match_dist = dist;
				}
			}

			// write best match to deliverable file
			out.write(queryData[n][0] + "," + queryData[n][1] + "," + pcds[match]);
			out.newLine();

			// write best match to maple file
			if (n!=0) out2.write(",");
			out2.write("[" + testData[match][0] + "," + testData[match][1] + "," + pcds[match] + "]");

		}

		// write footer
		out2.write("]:");

		// close files
		out.close();
		out2.close();
		} catch (IOException e) { e.printStackTrace();
		}

	}

///////////////////////////////////////////
//
// CALCULATE (COSINE OF) ANGLE BETWEEN POINTS
//
///////////////////////////////////////////

	public static double Angle(double lat1, double lon1, double lat2, double lon2) {

		double theta1, theta2; // the polar angles of the two points in radians
		double phi1, phi2; // the aximuthal angles of the two points in radians

		// calculates cos(psi) -- the cosine of the angle between the points (lat1,lon1) and (lat2,lon2)
        // (the distance between the two points is given by distance = r*psi, where r is the radius of the earth ---
        // the distance is minimised wherever cos(psi) is maximised)
		theta1 = Math.PI*lat1/180;
		theta2 = Math.PI*lat2/180;
		phi1 = Math.PI*(lon1)/180;
		phi2 = Math.PI*(lon2)/180;
		return Math.sin(theta1)*Math.sin(theta2) + Math.cos(theta1)*Math.cos(theta2)*Math.cos(phi1-phi2);

	}

///////////////////////////////////////////
//
// READ QUERY DATA
//
///////////////////////////////////////////

	public static void ReadFileQueries() {

		String fileIn = new String("test_queries.csv");
		String fileOut = new String("maple_data");
		String currentLine;
		double lat;
		double lon;
		int i=0,j=0,n=0; // counters

		try { // open file reader/writer
 
		BufferedReader in = new BufferedReader(new FileReader(fileIn));
		BufferedWriter out = new BufferedWriter(new FileWriter(fileOut,true));
 
		// read header
		currentLine = in.readLine();
		// write header
		out.write("Queries:=[");

		///////////////////////////////////////////////////////////////////
		// read fileIn, writing the data to the array queryData
		///////////////////////////////////////////////////////////////////

		currentLine = in.readLine();
		while (currentLine!= null) {
			
			// find latitude, longitude and postcode
			i=1;
			while ( i<currentLine.length() ) {
				i++;
				if(currentLine.substring(i-1,i).equals(","))break;
			}
			j=i; // i marks the start of latitude
			while ( j+1<currentLine.length() ) {
				j++;
				if(currentLine.substring(j,j+1).equals(","))break;
			}
			lat = Double.valueOf(currentLine.substring(i, j));
			lon = Double.valueOf(currentLine.substring(j+1, currentLine.length()));

			// save data to arrays queryData
			queryData[n][0] = lat;
			queryData[n][1] = lon;
			
			// write data to maple file
			if (n!=0) out.write(",");
			out.write("[" + lat + "," + lon + "]");

			// move on to next line
			currentLine = in.readLine();
			n++;
		}

		// write footer
		out.write("]:");
		out.newLine();

		System.out.println("Number of queries = " + n);

		// close files
		in.close();
		out.close();
		} catch (IOException e) { e.printStackTrace();
		}

	}

///////////////////////////////////////////
//
// READ ANNOTATED DATA
//
///////////////////////////////////////////

	public static void ReadFileTest() {

		String fileIn = new String("test_database.csv");
		String fileOut = new String("maple_data");
		String currentLine;
		double lat;
		double lon;
		int i=0,j=0,n=0; // counters

		try { // open file reader/writer
 
		BufferedReader in = new BufferedReader(new FileReader(fileIn));
		BufferedWriter out = new BufferedWriter(new FileWriter(fileOut,false));
 
		// read header
		currentLine = in.readLine();
		// write header
		out.write("Postcodes:=[");

		///////////////////////////////////////////////////////////////////
		// read fileIn, writing the data to the arrays testData and pcds
		///////////////////////////////////////////////////////////////////

		currentLine = in.readLine();
		while (currentLine!= null) {
			
			// find latitude, longitude and postcode
			i=1;
			while ( i<currentLine.length() ) {
				i++;
				if(currentLine.substring(i-1,i).equals(","))break;
			}
			j=i; // i marks the start of latitude
			while ( j+1<currentLine.length() ) {
				j++;
				if(currentLine.substring(j,j+1).equals(","))break;
			}
			lat = Double.valueOf(currentLine.substring(i, j));
			j++; i=j; // i marks the start of longitude
			while ( j+1<currentLine.length() ) {
				j++;
				if(currentLine.substring(j,j+1).equals(","))break;
			}
			lon = Double.valueOf(currentLine.substring(i, j));

			// save data to arrays testData/pcds
			testData[n][0] = lat;
			testData[n][1] = lon;
			pcds[n] = currentLine.substring(j+1, currentLine.length());
			
			// write data to maple file
			if (n!=0) out.write(",");
			out.write("[" + lat + "," + lon + "," + pcds[n] + "]");

			// move on to next line
			currentLine = in.readLine();
			n++;
		}

		// write footer
		out.write("]:");
		out.newLine();

		System.out.println("Number of records = " + n);

		// close files
		in.close();
		out.close();
		} catch (IOException e) { e.printStackTrace();
		}

	}

}
