/**
 * This module handles how information is reported to the user.
 * <p>
 * The user needs to see updates about:
 * <ul>
 * <li> Overall status of the application </li>
 * <li> TMS file loading to control the experiment </li>
 * <li> Audio file loading </li>
 * <li> Connection to the controller box via the serial port </li>
 * <li> Font update information </li>
 * <li> Whether the monitors have been turned on/off via controller box </li>
 * </ul>
 * Reporter class controls the state of this information by holding an
 * ObjectReport object for each of the above categories. It also receives any
 * updates from the other classes via the main Xmod.java class.
 * <br/>
 * ObjectReport class hold information for each category on overall status,
 * message, advice and any stacktrace. It is used by Reporter class and also is
 * the format by which other classes communicate updates to Reporter
 * <br/>
 * ReportCategory is an enum for the different types of information held by
 * Object Report
 * <br/>
 * ReportLabel is an enum for the different categories of information held by
 * Reporter
 * <br/>
 * Responses are string constants for different messages to the user
 * </p>
 *
 * @since 1.0
 * @author ELS
 * @version 1.1
 */

package xmod.status;
