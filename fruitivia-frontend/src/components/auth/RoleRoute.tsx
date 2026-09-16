import { Navigate, Outlet } from "react-router-dom"
import { useAuth } from "../../context/AuthContext"

interface RoleRouteProps {
  allowedRoles: string[]
}

export function RoleRoute({ allowedRoles }: RoleRouteProps) {
  const { hasRole, isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return <div className="p-8">Loading...</div>
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  const isAuthorized = allowedRoles.some((role) => hasRole(role))

  if (!isAuthorized) {
    return <Navigate to="/unauthorized" replace />
  }

  return <Outlet />
}
