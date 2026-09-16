import { Link } from "react-router-dom"
import { ShieldAlert } from "lucide-react"
import { Button } from "../../components/ui/button"

export function UnauthorizedPage() {
  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4">
      <div className="bg-card p-8 rounded-2xl shadow-xl max-w-md w-full text-center border border-border">
        <div className="flex justify-center mb-6">
          <div className="bg-destructive/10 p-4 rounded-full">
            <ShieldAlert className="w-12 h-12 text-destructive" />
          </div>
        </div>
        <h1 className="text-3xl font-heading font-bold text-foreground mb-2">Access Denied</h1>
        <p className="text-muted-foreground mb-8">
          You do not have the required permissions to access this page. Please contact your system administrator if you believe this is an error.
        </p>
        <Link to="/">
          <Button className="w-full">Return Home</Button>
        </Link>
      </div>
    </div>
  )
}
