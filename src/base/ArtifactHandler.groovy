/* an ArtifactHandler handles the build artifacts. (b
* [1] it moves the artifacts to a specified directory of a specified server (local or remote) before cleaning the workspace
* now if build is succesful and artifacts are generated and it failed to move the artifacts (for e.g, remote server is not reachable, directory does not exist etc.)
* [2] a backup mechanism needs to be trigrred (we do want to clean the workspace for next build)
*   [a] build artifacts should be moved to backup directory and must be deployable from there.
    [b] a notofication to responsible person , so that next build can go to waitaing state and issue can be resolved
    [c] if issue got resolved , build from backup directory must be moved to correct directory
* 
*/

package base


class ArtifactHandler extends Base {
  
  
  
}
