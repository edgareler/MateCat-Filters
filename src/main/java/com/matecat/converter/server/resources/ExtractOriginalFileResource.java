package com.matecat.converter.server.resources;

import com.matecat.converter.core.XliffProcessor;
import com.matecat.converter.core.project.Project;
import com.matecat.converter.core.project.ProjectFactory;
import com.matecat.converter.server.JSONResponseFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Resource taking care of the extraction of the original file from the .XLF
 *
 * The call is:
 * [POST]: /original/
 *
 * Sending the xlf file to convert as POST body with multipart/form-data encoding
 *
 * The result is returned as JSON, to obey the old Matecat library (TODO replace for file content)
 */
@Path("/original")
public class ExtractOriginalFileResource {

    // Logger
    private static Logger LOGGER = LoggerFactory.getLogger(ConvertToXliffResource.class);


    /**
     * Extract the original file from the xlf
     * @param fileInputStream Xlf
     * @return Original file
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    public Response convert(@FormDataParam("file") InputStream fileInputStream) {

        // Logging
        LOGGER.info("[EXTRACTION REQUEST]");

        Project project = null;
        Response response = null;
        try {

            // Check that the input file is not null
            if (fileInputStream == null)
                throw new IllegalArgumentException("The input file has not been sent");

            // Create the project
            project = ProjectFactory.createProject("to-original.xlf", fileInputStream);

            // Retrieve the xlf
            File originalFile = new XliffProcessor(project.getFile()).getOriginalFile();

            // Create response
            response = Response
                    .status(Response.Status.OK)
                    .entity(JSONResponseFactory.getSuccess(originalFile))
                    .build();
            LOGGER.info("[EXTRACTION REQUEST FINISHED]");
        }

        // If there is any error, return it
        catch (Exception e) {
            response = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(JSONResponseFactory.getError(e.getMessage()))
                    .build();
            LOGGER.error("[EXTRACTION REQUEST FAILED] {}", e.getMessage(), e);
        }

        // Close the project and streams
        finally {
            if (fileInputStream != null)
                try {
                    fileInputStream.close();
                } catch (IOException ignored) {}
            if (project != null)
                project.close();
        }

        return response;
    }

}
