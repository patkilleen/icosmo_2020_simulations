package test.phase.generation.cosmo;

import static org.junit.Assert.fail;

import org.junit.Test;

import common.Vehicle;
import common.exception.ConfigurationException;
import common.exception.SimulationException;
import junit.framework.Assert;
import phase.generation.cosmo.DistanceMatrix;

public class TestDistanceMatrix extends DistanceMatrix{

	@Test
	public void testDistanceMatrix() {
		DistanceMatrix matrix = new DistanceMatrix();
		Assert.assertEquals(0,matrix.numberoOfRows()); 
	}

	@Test
	public void testDistanceMatrixInt() {
		DistanceMatrix matrix = new DistanceMatrix(2);
		Assert.assertEquals(0,matrix.numberoOfRows());
	}

	@Test
	public void testDistanceMatrixInt_illegal_arg() {

		boolean flag = false;
		try{
			DistanceMatrix matrix = new DistanceMatrix(-1);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}


	@Test
	public void testSize_and_addRow() {
		DistanceMatrix matrix = new DistanceMatrix();
		Assert.assertEquals(0,matrix.numberoOfRows()); 
		double [] distances = new double[3];
		matrix.addRow(new Object(), new Vehicle(0), distances);

		Assert.assertEquals(1,matrix.numberoOfRows());

		matrix.addRow(new Object(), new Vehicle(0), distances);

		Assert.assertEquals(2,matrix.numberoOfRows());

		distances = new double[3];
		matrix.addRow(new Object(), new Vehicle(1), distances);

		Assert.assertEquals(3,matrix.numberoOfRows());

		matrix.addRow(new Object(), new Vehicle(1), distances);

		Assert.assertEquals(4,matrix.numberoOfRows());
	}

/*
	@Test
	public void testaddRow_illformed_row() {
		DistanceMatrix matrix = new DistanceMatrix();
		Assert.assertEquals(0,matrix.numberoOfRows()); 

		double [] distances = new double[3];

		//adding row 3, now  all next rows added need to be 3 of length
		matrix.addRow(new Object(), new Vehicle(0), distances);

		boolean flag = false;
		try{

			distances = new double[4];
			matrix.addRow(new Object(), new Vehicle(0), distances);

		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			distances = new double[2];
			matrix.addRow(new Object(), new Vehicle(0), distances);

		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			distances = new double[1];
			matrix.addRow(new Object(), new Vehicle(0), distances);

		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}
*/
	@Test
	public void testaddRow_illegal_arg() {
		DistanceMatrix matrix = new DistanceMatrix();
		Assert.assertEquals(0,matrix.numberoOfRows()); 

		double [] distances = new double[3];


		boolean flag = false;
		try{

			distances = new double[4];
			matrix.addRow(new Object(), null, distances);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			distances = new double[4];
			matrix.addRow(new Object(), new Vehicle(0), null);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{


			distances = new double[4];
			matrix.addRow(null, new Vehicle(0), distances);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

	}



	@Test
	public void testGetDistanceIntInt() {
		DistanceMatrix matrix = new DistanceMatrix();

		double [] distances = new double[3];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;

		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=3;
		distances[1]=4;
		distances[2]=5;

		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=6;
		distances[1]=7;
		distances[2]=8;

		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);


		//0 1 2
		//3 4 5
		//6 7 8
		Assert.assertEquals(0.0,matrix.getDistance(0, 0));
		Assert.assertEquals(1.0,matrix.getDistance(0, 1));
		Assert.assertEquals(2.0,matrix.getDistance(0, 2));
		Assert.assertEquals(3.0,matrix.getDistance(1, 0));
		Assert.assertEquals(4.0,matrix.getDistance(1, 1));
		Assert.assertEquals(5.0,matrix.getDistance(1, 2));
		Assert.assertEquals(6.0,matrix.getDistance(2, 0));
		Assert.assertEquals(7.0,matrix.getDistance(2, 1));
		Assert.assertEquals(8.0,matrix.getDistance(2, 2));
	}


	@Test
	public void testGetDistanceIntInt_illegal_arg() {
		DistanceMatrix matrix = new DistanceMatrix();

		double [] distances = new double[3];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;

		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=3;
		distances[1]=4;
		distances[2]=5;

		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=6;
		distances[1]=7;
		distances[2]=8;

		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		boolean flag = false;
		try{

			matrix.getDistance(-1, 0);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

		flag = false;
		try{

			matrix.getDistance(0, -1);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.getDistance(-1, -1);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.getDistance(-10, -100);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.getDistance(4, 4);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.getDistance(3,0);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.getDistance(0,3);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}
	@Test
	public void testGetDistanceObjectObject() {
		DistanceMatrix matrix = new DistanceMatrix();

		double [] distances = new double[3];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;

		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=3;
		distances[1]=4;
		distances[2]=5;

		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=6;
		distances[1]=7;
		distances[2]=8;

		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);


		//0 1 2
		//3 4 5
		//6 7 8
		Assert.assertEquals(0.0,matrix.getDistance(row1, row1));
		Assert.assertEquals(1.0,matrix.getDistance(row1, row2));
		Assert.assertEquals(2.0,matrix.getDistance(row1, row3));
		Assert.assertEquals(3.0,matrix.getDistance(row2, row1));
		Assert.assertEquals(4.0,matrix.getDistance(row2, row2));
		Assert.assertEquals(5.0,matrix.getDistance(row2, row3));
		Assert.assertEquals(6.0,matrix.getDistance(row3, row1));
		Assert.assertEquals(7.0,matrix.getDistance(row3, row2));
		Assert.assertEquals(8.0,matrix.getDistance(row3, row3));
	}



	@Test
	public void testGetDistanceObjectObject_row_key_not_found() {
		DistanceMatrix matrix = new DistanceMatrix();

		double [] distances = new double[3];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;

		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=3;
		distances[1]=4;
		distances[2]=5;

		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=6;
		distances[1]=7;
		distances[2]=8;

		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		boolean flag = false;
		try{

			matrix.getDistance(new Object(), row1);

		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.getDistance(row1,new Object());

		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.getDistance(new Object(), new Object());

		}catch(SimulationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

	}
	@Test
	public void testFilterVehicle1() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double [] distances = new double[4];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);

		distances = new double[4];
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);

		distances = new double[4];
		distances[0]=4;
		distances[1]=5;
		distances[2]=6;
		distances[3]=7;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[4];
		distances[0]=8;
		distances[1]=9;
		distances[2]=10;
		distances[3]=11;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		DistanceMatrix actualMatrix = matrix.filterVehicle(new Vehicle(1));
		//is
		//0 1 2 3
		//510 511 512 513
		//4 5 6 7
		//8 9 10 11

		//becomes (after removing the 2ns column and row)

		//0 2 3
		//4 6 7
		//8 10 11
		Assert.assertEquals(0.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(2.0,actualMatrix.getDistance(0, 1));
		Assert.assertEquals(3.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(4.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(7.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(8.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(10.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(11.0,actualMatrix.getDistance(2, 2));
		
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());

	}

	@Test
	public void testFilterVehicle2() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double [] distances = new double[4];
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);


		distances = new double[4];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);



		distances = new double[4];
		distances[0]=4;
		distances[1]=5;
		distances[2]=6;
		distances[3]=7;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[4];
		distances[0]=8;
		distances[1]=9;
		distances[2]=10;
		distances[3]=11;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		DistanceMatrix actualMatrix = matrix.filterVehicle(new Vehicle(1));
		//is
		//510 511 512 513
		//0 1 2 3
		//4 5 6 7
		//8 9 10 11

		//becomes (after removing the 1st column and row)

		//1 2 3
		//5 6 7
		//9 10 11
		Assert.assertEquals(1.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(2.0,actualMatrix.getDistance(0,1));
		Assert.assertEquals(3.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(5.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(7.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(9.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(10.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(11.0,actualMatrix.getDistance(2, 2));
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());
	}



	@Test
	public void testFilterVehicle3() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double []distances = new double[4];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);



		distances = new double[4];
		distances[0]=4;
		distances[1]=5;
		distances[2]=6;
		distances[3]=7;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[4];
		distances[0]=8;
		distances[1]=9;
		distances[2]=10;
		distances[3]=11;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		distances = new double[4];
		distances = new double[4];
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);
		
		
		DistanceMatrix actualMatrix = matrix.filterVehicle(new Vehicle(1));
		//is
		//0 1 2 3
		//4 5 6 7
		//8 9 10 11
		//510 511 512 513

		//becomes (after removing the last column and row)

		//0 1 2
		//4 5 6
		//8 9 10
		Assert.assertEquals(0.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(1.0,actualMatrix.getDistance(0, 1));
		Assert.assertEquals(2.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(4.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(5.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(8.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(9.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(10.0,actualMatrix.getDistance(2, 2));
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());
	}



	@Test
	public void testFilterVehicle4() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double []distances = new double[5];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);



		distances = new double[5];
		distances[0]=5;
		distances[1]=6;
		distances[2]=7;
		distances[3]=8;
		distances[4]=9;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[5];
		distances[0]=10;
		distances[1]=11;
		distances[2]=12;
		distances[3]=13;
		distances[4]=14;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		distances = new double[5];
		
		
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		distances[4]=514;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);
		
		
		
		distances = new double[5];
		distances[0]=610;
		distances[1]=611;
		distances[2]=612;
		distances[3]=613;
		distances[4]=614;
		Object rowDelete2 = new Object();
		matrix.addRow(rowDelete2, new Vehicle(1), distances);
		
		DistanceMatrix actualMatrix =  matrix.filterVehicle(new Vehicle(1));
		//is
		//0 1 2 3 4
		//5 6 7 8 9
		//10 11 12 13 14
		//510 511 512 513 613
		//610 611 612 613 614

		//becomes (after removing the last column and row)

		//0 1 2
		//5 6 7
		//10 11 12
		Assert.assertEquals(0.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(1.0,actualMatrix.getDistance(0, 1));
		Assert.assertEquals(2.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(5.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(7.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(10.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(11.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(12.0,actualMatrix.getDistance(2, 2));
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());
	}

	
	@Test
	public void testFilterVehicle5() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double []distances = new double[5];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);



		distances = new double[5];
		distances[0]=5;
		distances[1]=6;
		distances[2]=7;
		distances[3]=8;
		distances[4]=9;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[5];
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		distances[4]=514;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);
		
		distances = new double[5];
		distances[0]=10;
		distances[1]=11;
		distances[2]=12;
		distances[3]=13;
		distances[4]=14;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		
		
		
		distances = new double[5];
		distances[0]=610;
		distances[1]=611;
		distances[2]=612;
		distances[3]=613;
		distances[4]=614;
		Object rowDelete2 = new Object();
		matrix.addRow(rowDelete2, new Vehicle(1), distances);
		
		DistanceMatrix actualMatrix = 	matrix.filterVehicle(new Vehicle(1));
		//is
		//0 1 2 3 4
		//5 6 7 8 9
		//510 511 512 513 613
		//10 11 12 13 14
		//610 611 612 613 614

		//becomes (after removing the last column and row and 3rd column and row)

		//0 1 3
		//5 6 8
		//10 11 13
		Assert.assertEquals(0.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(1.0,actualMatrix.getDistance(0, 1));
		Assert.assertEquals(3.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(5.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(8.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(10.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(11.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(13.0,actualMatrix.getDistance(2, 2));
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());
	}
	
	
	@Test
	public void testFilterVehicle6() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double []distances = new double[5];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);



		distances = new double[5];
		distances[0]=5;
		distances[1]=6;
		distances[2]=7;
		distances[3]=8;
		distances[4]=9;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[5];
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		distances[4]=514;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);
		
		distances = new double[5];
		distances[0]=610;
		distances[1]=611;
		distances[2]=612;
		distances[3]=613;
		distances[4]=614;
		Object rowDelete2 = new Object();
		matrix.addRow(rowDelete2, new Vehicle(1), distances);
		
		distances = new double[5];
		distances[0]=10;
		distances[1]=11;
		distances[2]=12;
		distances[3]=13;
		distances[4]=14;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		
		
		
		
		
		DistanceMatrix actualMatrix = 	matrix.filterVehicle(new Vehicle(1));
		//is
		//0 1 2 3 4
		//5 6 7 8 9
		//510 511 512 513 613
		//610 611 612 613 614
		//10 11 12 13 14
		

		//becomes (after removing the 3rd and 4th column and row)

		//0 1 4
		//5 6 9
		//10 11 14
		Assert.assertEquals(0.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(1.0,actualMatrix.getDistance(0, 1));
		Assert.assertEquals(4.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(5.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(9.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(10.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(11.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(14.0,actualMatrix.getDistance(2, 2));
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());
	}
	
	@Test
	public void testFilterVehicle7() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double [] distances = new double[5];
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		distances[4]=514;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);
				
				
				
				
		distances = new double[5];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);



		distances = new double[5];
		distances[0]=5;
		distances[1]=6;
		distances[2]=7;
		distances[3]=8;
		distances[4]=9;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);


		
		distances = new double[5];
		distances[0]=610;
		distances[1]=611;
		distances[2]=612;
		distances[3]=613;
		distances[4]=614;
		Object rowDelete2 = new Object();
		matrix.addRow(rowDelete2, new Vehicle(1), distances);
		
		distances = new double[5];
		distances[0]=10;
		distances[1]=11;
		distances[2]=12;
		distances[3]=13;
		distances[4]=14;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		
		
		
		
		
		DistanceMatrix actualMatrix = 	matrix.filterVehicle(new Vehicle(1));
		//is
		//510 511 512 513 613
		//0 1 2 3 4
		//5 6 7 8 9
		//610 611 612 613 614
		//10 11 12 13 14
		

		//becomes (after removing the 1strd and 4th column and row)

		//1 2 4
		//6 7 9
		//11 12 14
		Assert.assertEquals(1.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(2.0,actualMatrix.getDistance(0, 1));
		Assert.assertEquals(4.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(7.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(9.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(11.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(12.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(14.0,actualMatrix.getDistance(2, 2));
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());
	}
	
	

	@Test
	public void testFilterVehicle8() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double [] distances = new double[5];
		distances[0]=510;
		distances[1]=511;
		distances[2]=512;
		distances[3]=513;
		distances[4]=514;
		Object rowDeleted = new Object();
		matrix.addRow(rowDeleted, new Vehicle(1), distances);
				
				
				
				
		distances = new double[5];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);



		distances = new double[5];
		distances[0]=5;
		distances[1]=6;
		distances[2]=7;
		distances[3]=8;
		distances[4]=9;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);


		
		
		distances = new double[5];
		distances[0]=10;
		distances[1]=11;
		distances[2]=12;
		distances[3]=13;
		distances[4]=14;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		

		distances = new double[5];
		distances[0]=610;
		distances[1]=611;
		distances[2]=612;
		distances[3]=613;
		distances[4]=614;
		Object rowDelete2 = new Object();
		matrix.addRow(rowDelete2, new Vehicle(1), distances);
		
		
		
		DistanceMatrix actualMatrix = matrix.filterVehicle(new Vehicle(1));
		//is
		//510 511 512 513 613
		//0 1 2 3 4
		//5 6 7 8 9
		//10 11 12 13 14
		//610 611 612 613 614
		

		//becomes (after removing the 1strd and last column and row)

		//1 2 3
		//6 7 8
		//11 12 13
		Assert.assertEquals(1.0,actualMatrix.getDistance(0, 0));
		Assert.assertEquals(2.0,actualMatrix.getDistance(0, 1));
		Assert.assertEquals(3.0,actualMatrix.getDistance(0, 2));
		Assert.assertEquals(6.0,actualMatrix.getDistance(1, 0));
		Assert.assertEquals(7.0,actualMatrix.getDistance(1, 1));
		Assert.assertEquals(8.0,actualMatrix.getDistance(1, 2));
		Assert.assertEquals(11.0,actualMatrix.getDistance(2, 0));
		Assert.assertEquals(12.0,actualMatrix.getDistance(2, 1));
		Assert.assertEquals(13.0,actualMatrix.getDistance(2, 2));
		Assert.assertEquals(3, actualMatrix.numberoOfRows());
		Assert.assertEquals(3, actualMatrix.numberOfColumns());
	}
	@Test
	public void testFilterVehicle_illegal_arg() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double [] distances = new double[3];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;

		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=3;
		distances[1]=4;
		distances[2]=5;

		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=6;
		distances[1]=7;
		distances[2]=8;

		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);


		boolean flag = false;
		try{

			matrix.filterVehicle(null);

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);


		flag = false;
		try{

			matrix.filterVehicle(new Vehicle(15));

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);
	}
	@Test
	public void testFindMinimumRow() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double [] distances = new double[3];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;

		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(1), distances);

		distances = new double[3];
		distances[0]=3;
		distances[1]=4;
		distances[2]=5;

		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=6;
		distances[1]=7;
		distances[2]=8;

		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		Row actual = matrix.findMinimumRow();
		Assert.assertEquals(true,row1 == actual.rowId);
		Assert.assertEquals(new Vehicle(1),actual.vehicle);
		Assert.assertEquals(0.0,actual.distances.get(0));
		Assert.assertEquals(1.0,actual.distances.get(1));
		Assert.assertEquals(2.0,actual.distances.get(2));


		matrix = new TestDistanceMatrix();

		distances = new double[3];
		distances[0]=10;
		distances[1]=12;
		distances[2]=12;

		row1 = new Object();
		matrix.addRow(row1, new Vehicle(1), distances);

		distances = new double[3];
		distances[0]=3;
		distances[1]=4;
		distances[2]=5;

		row2 = new Object();
		matrix.addRow(row2, new Vehicle(0), distances);

		distances = new double[3];
		distances[0]=68;
		distances[1]=7;
		distances[2]=18;

		row3 = new Object();
		matrix.addRow(row3, new Vehicle(0), distances);

		actual = matrix.findMinimumRow();
		Assert.assertEquals(true,row2 == actual.rowId);
		Assert.assertEquals(true,new Vehicle(0).equals(actual.vehicle));
		Assert.assertEquals(3.0,actual.distances.get(0));
		Assert.assertEquals(4.0,actual.distances.get(1));
		Assert.assertEquals(5.0,actual.distances.get(2));


	}

	@Test
	public void testFindMinimumRow_empty_matrix() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		boolean flag = false;
		try{

			Row row = matrix.findMinimumRow();

		}catch(ConfigurationException e){
			flag = true;
		}

		Assert.assertEquals(true,flag);

	}

	@Test
	public void testFindMinimumRow_1_row() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();

		double [] distances = new double[3];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;

		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(1), distances);

		Row actual = matrix.findMinimumRow();
		Assert.assertEquals(true,row1 == actual.rowId);
		Assert.assertEquals(new Vehicle(1),actual.vehicle);
		Assert.assertEquals(0.0,actual.distances.get(0));
		Assert.assertEquals(1.0,actual.distances.get(1));
		Assert.assertEquals(2.0,actual.distances.get(2));


	}
	@Test
	public void testMostCentralPattern_c_first_row() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();
		double [] distances = new double[5];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);
					
				
		distances = new double[5];
		distances[0]=1;
		distances[1]=0;
		distances[2]=5;
		distances[3]=6;
		distances[4]=7;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(1), distances);



		distances = new double[5];
		distances[0]=2;
		distances[1]=5;
		distances[2]=0;
		distances[3]=8;
		distances[4]=9;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(2), distances);


		
		
		distances = new double[5];
		distances[0]=3;
		distances[1]=6;
		distances[2]=8;
		distances[3]=0;
		distances[4]=10;
		Object row4 = new Object();
		matrix.addRow(row4, new Vehicle(3), distances);

		

		distances = new double[5];
		distances[0]=4;
		distances[1]=7;
		distances[2]=9;
		distances[3]=10;
		distances[4]=0;
		Object row5 = new Object();
		matrix.addRow(row5, new Vehicle(4), distances);
		

		//note that row1 is the most central (minimum sum row)
		//recall zvalu = (|{i = 1,...,Ns}: d_ic > d_mc|)/Ns
		//where Ns is size of submatrix after remove the test model and all models of same vehicle as test model from matrix
		//d_ic is distance from model i to most central (row 1)
		//d_mc is the distance of target model to most central
		double actual = matrix.mostCentralPattern(row2);
		Assert.assertEquals(1.0, actual,0.0001);
		
		
		actual = matrix.mostCentralPattern(row3);
		double expected = 2.0/3.0;
		Assert.assertEquals(expected, actual,0.0001);
		
		expected = 1.0/3.0;
		actual = matrix.mostCentralPattern(row4);
		Assert.assertEquals(expected, actual,0.0001);
		
		actual = matrix.mostCentralPattern(row5);
		expected = 0.0;
		Assert.assertEquals(expected, actual,0.0001);
		
		actual = matrix.mostCentralPattern(row1);
		Assert.assertEquals(1.0, actual,0.0001);
		
	}


	@Test
	public void testMostCentralPattern_same_vehicles() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();
		double [] distances = new double[5];
		distances[0]=0;
		distances[1]=1;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);
					
				
		distances = new double[5];
		distances[0]=1;
		distances[1]=0;
		distances[2]=5;
		distances[3]=6;
		distances[4]=7;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(1), distances);



		distances = new double[5];
		distances[0]=2;
		distances[1]=5;
		distances[2]=0;
		distances[3]=8;
		distances[4]=9;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(2), distances);


		
		
		distances = new double[5];
		distances[0]=3;
		distances[1]=6;
		distances[2]=8;
		distances[3]=0;
		distances[4]=10;
		Object row4 = new Object();
		matrix.addRow(row4, new Vehicle(3), distances);

		

		distances = new double[5];
		distances[0]=4;
		distances[1]=7;
		distances[2]=9;
		distances[3]=10;
		distances[4]=0;
		Object row5 = new Object();
		matrix.addRow(row5, new Vehicle(1), distances);
		

		//note that row1 is the most central (minimum sum row)
		//recall zvalu = (|{i = 1,...,Ns}: d_ic > d_mc|)/Ns
		//where Ns is size of submatrix after remove the test model and all models of same vehicle as test model from matrix
		//d_ic is distance from model i to most central (row 1)
		//d_mc is the distance of target model to most central
		double actual = matrix.mostCentralPattern(row2);
		Assert.assertEquals(1.0, actual,0.0001);
		
		
		actual = matrix.mostCentralPattern(row3);
		double expected = 2.0/3.0;
		Assert.assertEquals(expected, actual,0.0001);
		
		expected = 1.0/3.0;
		actual = matrix.mostCentralPattern(row4);
		Assert.assertEquals(expected, actual,0.0001);
		
		actual = matrix.mostCentralPattern(row5);
		expected = 0.0;
		Assert.assertEquals(expected, actual,0.0001);
		
		actual = matrix.mostCentralPattern(row1);
		expected = 1.0;
		Assert.assertEquals(expected, actual,0.0001);
		
	}
	
	@Test
	public void testMostCentralPattern_c_mid_row() {
		TestDistanceMatrix matrix = new TestDistanceMatrix();
		double [] distances = new double[5];
		distances[0]=0;
		distances[1]=16;
		distances[2]=2;
		distances[3]=3;
		distances[4]=4;
		Object row1 = new Object();
		matrix.addRow(row1, new Vehicle(0), distances);
					
				
		distances = new double[5];
		distances[0]=16;
		distances[1]=0;
		distances[2]=5;
		distances[3]=6;
		distances[4]=7;
		Object row2 = new Object();
		matrix.addRow(row2, new Vehicle(1), distances);



		distances = new double[5];
		distances[0]=2;
		distances[1]=5;
		distances[2]=0;
		distances[3]=8;
		distances[4]=9;
		Object row3 = new Object();
		matrix.addRow(row3, new Vehicle(2), distances);


		
		
		distances = new double[5];
		distances[0]=3;
		distances[1]=6;
		distances[2]=8;
		distances[3]=0;
		distances[4]=10;
		Object row4 = new Object();
		matrix.addRow(row4, new Vehicle(3), distances);

		

		distances = new double[5];
		distances[0]=4;
		distances[1]=7;
		distances[2]=9;
		distances[3]=10;
		distances[4]=0;
		Object row5 = new Object();
		matrix.addRow(row5, new Vehicle(4), distances);
		

		//note that row3 is the most central (minimum sum row)
		//recall zvalu = (|{i = 1,...,Ns}: d_ic > d_mc|)/Ns
		//where Ns is size of submatrix after remove the test model and all models of same vehicle as test model from matrix
		//d_ic is distance from model i to most central (row 1)
		//d_mc is the distance of target model to most central
		double actual = matrix.mostCentralPattern(row1);
		Assert.assertEquals(0.0, actual,0.0001);
		
		
		actual = matrix.mostCentralPattern(row2);
		double expected = 0.0;
		Assert.assertEquals(expected, actual,0.0001);
		
		expected = 1.0/3.0;
		actual = matrix.mostCentralPattern(row3);
		Assert.assertEquals(expected, actual,0.0001);
		
		expected = 1.0/3.0;
		actual = matrix.mostCentralPattern(row4);
		Assert.assertEquals(expected, actual,0.0001);
		
		actual = matrix.mostCentralPattern(row5);
		expected = 0.0/3.0;
		Assert.assertEquals(expected, actual,0.0001);
		
	}

}
