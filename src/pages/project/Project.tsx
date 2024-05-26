import { useParams } from 'react-router-dom'

export function Project() {
  const params = useParams()
  const paramsId = params.projectId

  return (
    <div>
      <p>paramsId: {paramsId}</p>
    </div>
  )
}
