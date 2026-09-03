// Mirrors the real backend DTOs (see backend/src/main/java/.../resource,
// booking, incident, review, analytics, auth). Kept intentionally close
// to the Java records/entities so a field rename on one side is easy to
// spot on the other.

export type ResourceType = 'HALL' | 'LAB' | 'ROOM' | 'EQUIPMENT'

export interface Resource {
  id: string
  name: string
  type: ResourceType
  description?: string
  capacity: number
  facilities: string[]
  active: boolean
}

export type BookingStatus = 'REQUESTED' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

export interface Booking {
  id: string
  resourceId: string
  seatNumbers: string[]
  startTime: string
  endTime: string
  status: BookingStatus
  requesterName: string
  requesterEmail: string
  ticketCode?: string
}

export type IncidentStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'
export type IncidentSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export interface Incident {
  id: string
  resourceId: string
  title: string
  description: string
  severity: IncidentSeverity
  status: IncidentStatus
  reporterName: string
  reporterEmail: string
  assignedTechnician?: string
}

export interface Review {
  id: string
  resourceId: string
  rating: number
  comment?: string
  reviewerName: string
  reviewerEmail: string
  createdAt: string
}

export interface ResourceRating {
  resourceId: string
  averageRating: number
  reviewCount: number
}

export interface CurrentUser {
  email: string
  name: string
  roles: { authority: string }[]
}

export interface AnalyticsSummary {
  totalResources: number
  totalBookings: number
  bookingsByStatus: Record<string, number>
  totalIncidents: number
  incidentsByStatus: Record<string, number>
  averageIncidentResolutionMinutes: number | null
  totalReviews: number
  overallAverageRating: number
}
