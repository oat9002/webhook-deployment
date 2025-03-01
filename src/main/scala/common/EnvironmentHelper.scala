package common

object EnvironmentHelper {
  def isDevelopment: Boolean = {
    val env = System.getenv().getOrDefault("env", "development")
    "development".equals(env)
  }
}
