enum ServerType {
  LOCAL,  // This represent localhost (controller server or if master-salve architecture is being used then slave server)
  REMOTE  // a remote server (in context of ArtifactHandler , any other server apart from where artifcats was generated)
}
