/* Copyright 2009-2016 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.examples.ga.tsplib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A TSPLIB problem instance.
 */
public class TSPInstance {
	
	/**
	 * The name of this problem instance.
	 */
	private String name;
	
	/**
	 * The type of this problem instance.
	 */
	private DataType dataType;
	
	/**
	 * Any comments about this problem instance.
	 */
	private String comment;
	
	/**
	 * The number of nodes defined by this problem instance.
	 */
	private int dimension;
	
	/**
	 * The truck capacity in CVRP problem instances.
	 */
	private int capacity;
	
	/**
	 * The way edge weights are specified.
	 */
	private EdgeWeightType edgeWeightType;
	
	/**
	 * The format of the edge weight matrix when explicit weights are used; or
	 * {@code null} if edge weights are not explicit.
	 */
	private EdgeWeightFormat edgeWeightFormat;
	
	/**
	 * The format of edge data; or {@code null} if edge data is not explicitly
	 * defined.
	 */
	private EdgeDataFormat edgeDataFormat;
	
	/**
	 * The format of node coordinate data.
	 */
	private NodeCoordType nodeCoordinateType;
	
	/**
	 * The way graphical displays of the data should be generated.
	 */
	private DisplayDataType displayDataType;
	
	/**
	 * The distance table that defines the nodes, edges, and weights for this
	 * problem instance.
	 */
	private DistanceTable distanceTable;
	
	/**
	 * The data used to graphically display the nodes; or {@code null} if the
	 * display data is not explicitly defined.
	 */
	private NodeCoordinates displayData;
	
	/**
	 * The edges that are required in each solution to this problem instance.
	 */
	private EdgeData fixedEdges;
	
	/**
	 * The solutions to this problem instance.
	 */
	private List<Tour> tours;
	
	/**
	 * The demands and depot nodes for vehicle routing problems; or {@code null}
	 * if this is not a vehicle routing problem instance.
	 */
	private VehicleRoutingTable vehicleRoutingTable;
	
	/**
	 * Constructs a new, empty TSPLIB problem instance.
	 */
	public TSPInstance() {
		super();
		
		tours = new ArrayList<Tour>();
	}
	
	/**
	 * Constructs a TSPLIB problem instance from the specified TSPLIB file.
	 * 
	 * @param file the TSPLIB file defining the problem
	 * @throws IOException if an I/O error occurred while loading the TSPLIB
	 *         file
	 */
	public TSPInstance(File file) throws IOException {
		this();
		load(file);
	}
	
	/**
	 * Constructs a TSPLIB problem instance from the specified reader
	 * containing TSPLIB data.  The reader is NOT closed by this method.
	 * 
	 * @param reader the reader containing the TSPLIB data
	 * @throws IOException if an I/O error occurred while loading the TSPLIB
	 *         data
	 */
	public TSPInstance(Reader reader) throws IOException {
		this();
		load(new BufferedReader(reader));
	}
	
	/**
	 * Loads a problem instance from the specified TSPLIB file.
	 * 
	 * @param file the TSPLIB file defining the problem
	 * @throws IOException if an I/O error occurred while loading the TSPLIB
	 *         file
	 */
	protected void load(File file) throws IOException {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			load(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Loads a problem instance from the specified reader.
	 * 
	 * @param reader the reader containing the TSPLIB data
	 * @throws IOException if an I/O error occurred while loading the TSPLIB
	 *         data
	 */
	protected void load(BufferedReader reader) throws IOException {
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			line = line.trim();

			if (line.equals("NODE_COORD_SECTION")) {
				if (nodeCoordinateType == null) {
					nodeCoordinateType = edgeWeightType.getNodeCoordType();
				}

				distanceTable = new NodeCoordinates(dimension, edgeWeightType);
				distanceTable.load(reader);
			} else if (line.equals("EDGE_WEIGHT_SECTION")) {
				if (DataType.SOP.equals(dataType)) {
					// for whatever reason, SOP instances have an extra line with
					// the node count
					reader.readLine();
				}

				distanceTable = new EdgeWeightMatrix(dimension, edgeWeightFormat);
				distanceTable.load(reader);
			} else if (line.equals("EDGE_DATA_SECTION")) {
				distanceTable = new EdgeData(dimension, edgeDataFormat);
				distanceTable.load(reader);
			} else if (line.equals("DISPLAY_DATA_SECTION")) {
				displayData = new NodeCoordinates(dimension, NodeCoordType.TWOD_COORDS, null);
				displayData.load(reader);
			} else if (line.equals("TOUR_SECTION") || line.equals("-1")) {
				Tour tour = new Tour();
				tour.load(reader);
				tours.add(tour);
			} else if (line.equals("FIXED_EDGES_SECTION") || line.matches("^\\s*FIXED_EDGES\\s*\\:\\s*$")) {
				fixedEdges = new EdgeData(dimension, EdgeDataFormat.EDGE_LIST);
				fixedEdges.load(reader);
			} else if (line.equals("DEMAND_SECTION")) {
				if (vehicleRoutingTable == null) {
					vehicleRoutingTable = new VehicleRoutingTable(dimension);
				}

				vehicleRoutingTable.loadDemands(reader);
			} else if (line.equals("DEPOT_SECTION")) {
				if (vehicleRoutingTable == null) {
					vehicleRoutingTable = new VehicleRoutingTable(dimension);
				}

				vehicleRoutingTable.loadDepots(reader);
			} else if (line.equals("EOF")) {
				break;
			} else if (line.isEmpty()) {
				//do nothing
			} else {
				String[] tokens = line.split(":");
				String key = tokens[0].trim();
				String value = tokens[1].trim();

				if (key.equals("NAME")) {
					name = value;
				} else if (key.equals("COMMENT")) {
					if (comment == null) {
						comment = value;
					} else {
						comment = comment + "\n" + value;
					}
				} else if (key.equals("TYPE")) {
					dataType = DataType.valueOf(value);
				} else if (key.equals("DIMENSION")) {
					dimension = Integer.parseInt(value);
				} else if (key.equals("CAPACITY")) {
					capacity = Integer.parseInt(value);
				} else if (key.equals("EDGE_WEIGHT_TYPE")) {
					edgeWeightType = EdgeWeightType.valueOf(value);
				} else if (key.equals("EDGE_WEIGHT_FORMAT")) {
					edgeWeightFormat = EdgeWeightFormat.valueOf(value);
				} else if (key.equals("EDGE_DATA_FORMAT")) {
					edgeDataFormat = EdgeDataFormat.valueOf(value);
				} else if (key.equals("NODE_COORD_FORMAT")) {
					nodeCoordinateType = NodeCoordType.valueOf(value);
				} else if (key.equals("DISPLAY_DATA_TYPE")) {
					displayDataType = DisplayDataType.valueOf(value);
				}
			}
		}
		
		// fill in default settings
		if (nodeCoordinateType == null) {
			nodeCoordinateType = NodeCoordType.NO_COORDS;
		}
		
		if (displayDataType == null) {
			if (NodeCoordType.NO_COORDS.equals(nodeCoordinateType)) {
				displayDataType = DisplayDataType.NO_DISPLAY;
			} else if (displayData != null) {
				displayDataType = DisplayDataType.TWOD_DISPLAY;
			} else {
				displayDataType = DisplayDataType.COORD_DISPLAY;
			} 
		}
	}
	
	/**
	 * Adds a solution to this TSPLIB problem instance.  This method does not
	 * verify that the solution has all required edges; the caller must ensure
	 * this condition holds.
	 * 
	 * @param tour the solution to add
	 */
	public void addTour(Tour tour) {
		tours.add(tour);
	}
	
	/**
	 * Adds a solution to this TSPLIB problem instance that is defined in a
	 * separate file.  This method does not verify that the solution is a
	 * valid tour for this problem instance; the caller must ensure this
	 * condition holds.
	 * 
	 * @param file the file containing a solution to this TSPLIB problem
	 *        instance
	 * @throws IOException if an I/O error occurred while loading the tour
	 */
	public void addTour(File file) throws IOException {
		TSPInstance problem = new TSPInstance(file);
		
		if (problem.getDataType().equals(DataType.TOUR)) {
			tours.addAll(problem.getTours());
		} else {
			throw new IllegalArgumentException("not a tour file");
		}
	}
	
	/**
	 * Returns the name of this problem instance.
	 * 
	 * @return the name of this problem instance
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of this problem instance.
	 * 
	 * @return the type of this problem instance
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Returns any comments about this problem instance.
	 * 
	 * @return any comments about this problem instance
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Returns the number of nodes defined by this problem instance.
	 * 
	 * @return the number of nodes defined by this problem instance
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Returns the truck capacity in CVRP problem instances.  The return value
	 * is undefined if the data type is not {@code CVRP}.
	 * 
	 * @return the truck capacity in CVRP problem instances
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns the way edge weights are specified.
	 * 
	 * @return the way edge weights are specified
	 */
	public EdgeWeightType getEdgeWeightType() {
		return edgeWeightType;
	}

	/**
	 * Returns the format of the edge weight matrix when explicit weights are
	 * used; or {@code null} if edge weights are not explicit.
	 * 
	 * @return the format of the edge weight matrix when explicit weights are
	 *         used; or {@code null} if edge weights are not explicit
	 */
	public EdgeWeightFormat getEdgeWeightFormat() {
		return edgeWeightFormat;
	}

	/**
	 * Returns the format of edge data; or {@code null} if edge data is not
	 * explicitly defined.
	 * 
	 * @return the format of edge data; or {@code null} if edge data is not
	 *         explicitly defined
	 */
	public EdgeDataFormat getEdgeDataFormat() {
		return edgeDataFormat;
	}

	/**
	 * Returns the format of node coordinate data.
	 * 
	 * @return the format of node coordinate data
	 */
	public NodeCoordType getNodeCoordinateType() {
		return nodeCoordinateType;
	}

	/**
	 * Returns the way graphical displays of the data should be generated.
	 * 
	 * @return the way graphical displays of the data should be generated
	 */
	public DisplayDataType getDisplayDataType() {
		return displayDataType;
	}

	/**
	 * Returns the distance table that defines the nodes, edges, and weights
	 * for this problem instance.
	 * 
	 * @return the distance table that defines the nodes, edges, and weights
	 *         for this problem instance
	 */
	public DistanceTable getDistanceTable() {
		return distanceTable;
	}

	/**
	 * Returns the data used to graphically display the nodes; or {@code null}
	 * if the display data is not explicitly defined.
	 * 
	 * @return the data used to graphically display the nodes; or {@code null}
	 *         if the display data is not explicitly defined
	 */
	public NodeCoordinates getDisplayData() {
		return displayData;
	}

	/**
	 * Returns the edges that are required in each solution to this problem
	 * instance.
	 * 
	 * @return the edges that are required in each solution to this problem
	 * instance
	 */
	public EdgeData getFixedEdges() {
		return fixedEdges;
	}

	/**
	 * Returns the solutions to this problem instance.
	 * 
	 * @return the solutions to this problem instance
	 */
	public List<Tour> getTours() {
		return tours;
	}

	/**
	 * Returns the demands and depot nodes for vehicle routing problems; or
	 * {@code null} if this is not a vehicle routing problem instance.
	 * 
	 * @return the demands and depot nodes for vehicle routing problems; or
	 *         {@code null} if this is not a vehicle routing problem instance
	 */
	public VehicleRoutingTable getVehicleRoutingTable() {
		return vehicleRoutingTable;
	}

}
