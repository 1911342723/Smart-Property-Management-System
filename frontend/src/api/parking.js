import request from '@/utils/request'

export function getParkingSpaceList(params) {
  return request({
    url: '/parking-space/list',
    method: 'get',
    params
  })
}

export function getParkingSpaceDetail(id) {
  return request({
    url: `/parking-space/${id}`,
    method: 'get'
  })
}

export function createParkingSpace(data) {
  return request({
    url: '/parking-space',
    method: 'post',
    data
  })
}

export function updateParkingSpace(id, data) {
  return request({
    url: `/parking-space/${id}`,
    method: 'put',
    data
  })
}

export function deleteParkingSpace(id) {
  return request({
    url: `/parking-space/${id}`,
    method: 'delete'
  })
}

export function batchDeleteParkingSpace(ids) {
  return request({
    url: '/parking-space/batch',
    method: 'delete',
    data: ids
  })
}