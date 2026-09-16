import { Outlet, NavLink } from "react-router-dom"
import type { LucideIcon } from "lucide-react"
import { LogOut, Leaf } from "lucide-react"
import { useAuth } from "../../context/AuthContext"

export interface NavItem {
  name: string
  path: string
  icon: LucideIcon
}

interface AppLayoutProps {
  navItems: NavItem[]
  title: string
}

export function AppLayout({ navItems, title }: AppLayoutProps) {
  const { logout } = useAuth()

  return (
    <div className="flex h-screen bg-background font-sans text-foreground">
      {/* Sidebar */}
      <aside className="w-64 bg-card border-r border-border flex flex-col shadow-sm z-10">
        <div className="p-6 flex items-center gap-3 border-b border-border">
          <div className="bg-primary p-1.5 rounded-lg">
            <Leaf className="w-6 h-6 text-secondary" />
          </div>
          <span className="text-xl font-heading font-bold tracking-tight text-primary">Fruitivia {title}</span>
        </div>

        <nav className="flex-1 p-4 flex flex-col gap-1">
          {navItems.map((item) => {
            const Icon = item.icon
            return (
              <NavLink
                key={item.path}
                to={item.path}
                end={item.path.split('/').length <= 2}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-colors ${
                    isActive
                      ? "bg-primary/10 text-primary shadow-sm"
                      : "text-muted-foreground hover:bg-muted hover:text-primary"
                  }`
                }
              >
                {({ isActive }) => (
                  <>
                    <Icon className={`w-5 h-5 ${isActive ? "text-primary" : "text-muted-foreground"}`} />
                    {item.name}
                  </>
                )}
              </NavLink>
            )
          })}
        </nav>

        <div className="p-4 border-t border-border">
          <button 
            onClick={logout}
            className="flex items-center gap-3 px-4 py-3 w-full rounded-xl font-medium text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors"
          >
            <LogOut className="w-5 h-5" />
            Sign Out
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto bg-muted/20">
        <header className="h-16 border-b border-border bg-card flex items-center px-8 shadow-sm">
           <h2 className="text-xl font-heading font-semibold text-primary">{title} Dashboard</h2>
        </header>
        <div className="p-8">
           <Outlet />
        </div>
      </main>
    </div>
  )
}
