import request from '@/utils/request'

export function getParkingList(params) {
  return request({
    url: '/api/parking/list',
    method: 'get',
    params
  })
}

export function getParkingInfo(id) {
  return request({
    url: `/api/parking/${id}`,
    method: 'get'
  })
}

export function addParking(data) {
  return request({
    url: '/api/parking/add',
    method: 'post',
    data
  })
}

export function updateParking(data) {
  return request({
    url: '/api/parking/update',
    method: 'put',
    data
  })
}

export function deleteParking(ids) {
  return request({
    url: `/api/parking/${ids}`,
    method: 'delete'
  })
}
