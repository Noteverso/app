import { useParams } from 'react-router-dom'

export default function Project() {
  const params = useParams()
  const paramsId = params.projectId

  return (
    <div>
      <h1>Project</h1>
      <p>paramsId: {paramsId}</p>
    </div>
  )
}
