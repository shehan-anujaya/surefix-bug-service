// PM2 process definition for bug-service.
// Started by the GCE startup script; environment variables come from /etc/surefix/env.
module.exports = {
  apps: [
    {
      name: "bug-service",
      script: "/usr/bin/java",
      args: "-jar /opt/surefix/bug-service.jar",
      interpreter: "none",
      cwd: "/opt/surefix",
      autorestart: true,            // restart automatically if the process dies
      restart_delay: 5000,
      exp_backoff_restart_delay: 2000,
      max_restarts: 1000,
      out_file: "/var/log/surefix/bug-service.out.log",
      error_file: "/var/log/surefix/bug-service.err.log",
      merge_logs: true,
      time: true
    }
  ]
};
